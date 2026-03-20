package com.news.skynet.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * ChatViewModel
 *
 * Drives the AI chat screen. It manages the on-device TLM lifecycle using
 * MediaPipe's [LlmInference] API, which supports models such as:
 *   • Gemma 3 1B INT4 (~600 MB)  — recommended for most devices
 *   • Gemma 2 2B INT4 (~1.4 GB)  — higher quality on mid-range+
 *
 * The model binary (.bin/.task) must be placed in the app's private files dir
 * (e.g. `adb push gemma-3-1b-it-int4.task /data/data/com.news.skynet/files/`).
 * If absent the [ChatUiState.ModelNotFound] state is shown with download guidance.
 *
 * Generation uses streaming callbacks so tokens appear in real-time.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ── State ─────────────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.ModelNotFound)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _setupState = MutableStateFlow<ModelSetupState>(ModelSetupState.Idle)
    val setupState: StateFlow<ModelSetupState> = _setupState.asStateFlow()

    // ── Private ───────────────────────────────────────────────────────────────

    private var llmInference: LlmInference? = null

    // Conversation context — kept short to stay within token budget
    private val history = mutableListOf<ChatMessage>()

    companion object {
        const val MODEL_FILENAME = "gemma-3-1b-it-int4.task"
        private const val MAX_TOKENS = 1024
        private const val TEMPERATURE = 0.7f
        private const val TOP_K = 40

        /**
         * Download URL for Gemma 3 1B INT4 — users can supply their own from
         * https://huggingface.co/google/gemma-3-1b-it
         * Replace with a direct-download CDN link in production.
         */
        const val MODEL_DOWNLOAD_URL =
            "https://huggingface.co/google/gemma-3-1b-it/resolve/main/gemma-3-1b-it-int4.task"
    }

    init {
        checkModelAndLoad()
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun checkModelAndLoad() {
        val modelFile = File(context.filesDir, MODEL_FILENAME)
        if (!modelFile.exists()) {
            _uiState.value = ChatUiState.ModelNotFound
            return
        }
        loadModel(modelFile.absolutePath)
    }

    fun sendMessage(userText: String) {
        if (_uiState.value !is ChatUiState.Ready) return
        val trimmed = userText.trim()
        if (trimmed.isBlank()) return

        val userMsg = ChatMessage(text = trimmed, role = ChatMessage.Role.USER)
        history.add(userMsg)
        _messages.update { it + userMsg }

        // Placeholder streaming message
        val assistantMsg = ChatMessage(
            text = "", role = ChatMessage.Role.ASSISTANT, isStreaming = true
        )
        _messages.update { it + assistantMsg }
        _uiState.value = ChatUiState.Generating

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt = buildPrompt(trimmed)
                val buffer = StringBuilder()

                llmInference?.generateResponseAsync(prompt) { partial, done ->
                    buffer.append(partial)
                    // Update the last (streaming) message in-place
                    _messages.update { list ->
                        list.dropLast(1) + assistantMsg.copy(
                            text = buffer.toString(),
                            isStreaming = !done
                        )
                    }
                    if (done) {
                        val finalMsg = ChatMessage(
                            id = assistantMsg.id,
                            text = buffer.toString(),
                            role = ChatMessage.Role.ASSISTANT,
                            isStreaming = false
                        )
                        history.add(finalMsg)
                        _uiState.value = ChatUiState.Ready
                    }
                } ?: run {
                    appendErrorMessage("Model not loaded. Please restart the chat.")
                }
            } catch (e: Exception) {
                appendErrorMessage("Error: ${e.localizedMessage}")
            }
        }
    }

    fun clearConversation() {
        history.clear()
        _messages.value = emptyList()
    }

    fun retryLoadModel() = checkModelAndLoad()

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun loadModel(modelPath: String) {
        _uiState.value = ChatUiState.ModelLoading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelPath)
                    .setMaxTokens(MAX_TOKENS)
                    .build()

                llmInference = withContext(Dispatchers.IO) {
                    LlmInference.createFromOptions(context, options)
                }

                _uiState.value = ChatUiState.Ready

                // Greet the user
                val greeting = ChatMessage(
                    text = "Hi! I'm SkyNet AI, your on-device news assistant. " +
                           "Ask me anything about current events, news topics, or " +
                           "have me summarize a story for you.",
                    role = ChatMessage.Role.ASSISTANT
                )
                _messages.update { listOf(greeting) }

            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(
                    "Failed to load model: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Builds a Gemma-compatible instruction prompt.
     *
     * Format (Gemma 3 instruction-tuned):
     * ```
     * <start_of_turn>user
     * {question}<end_of_turn>
     * <start_of_turn>model
     * ```
     */
    private fun buildPrompt(userText: String): String {
        val sb = StringBuilder()

        // System context (injected as the first user turn for Gemma)
        if (history.size <= 1) {
            sb.append("<start_of_turn>user\n")
            sb.append("You are SkyNet AI, a helpful news assistant embedded in the ")
            sb.append("SkyNet Reader Android app. Answer questions about news topics, ")
            sb.append("current events, and help summarize articles. Be concise and factual.")
            sb.append("<end_of_turn>\n<start_of_turn>model\n")
            sb.append("Understood. I'm ready to help with news questions.<end_of_turn>\n")
        }

        // Recent history (last 6 turns to keep within context window)
        val window = history.takeLast(6)
        for (msg in window.dropLast(1)) {          // already added current user turn
            val tag = if (msg.role == ChatMessage.Role.USER) "user" else "model"
            sb.append("<start_of_turn>$tag\n${msg.text}<end_of_turn>\n")
        }

        // Current user turn
        sb.append("<start_of_turn>user\n$userText<end_of_turn>\n")
        sb.append("<start_of_turn>model\n")

        return sb.toString()
    }

    private fun appendErrorMessage(text: String) {
        _messages.update { list ->
            // Replace last streaming message with error text
            if (list.lastOrNull()?.isStreaming == true) {
                list.dropLast(1) + ChatMessage(
                    text = text, role = ChatMessage.Role.SYSTEM
                )
            } else {
                list + ChatMessage(text = text, role = ChatMessage.Role.SYSTEM)
            }
        }
        _uiState.value = ChatUiState.Ready
    }

    override fun onCleared() {
        super.onCleared()
        llmInference?.close()
    }
}
