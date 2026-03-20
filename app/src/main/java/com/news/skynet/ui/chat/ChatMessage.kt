package com.news.skynet.ui.chat

/**
 * Represents a single message in the AI chat conversation.
 */
data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val role: Role,
    val isStreaming: Boolean = false
) {
    enum class Role { USER, ASSISTANT, SYSTEM }
}

/**
 * Sealed state for the Chat UI.
 */
sealed class ChatUiState {
    /** On-device model file not found — prompt the user to download it. */
    object ModelNotFound : ChatUiState()

    /** Model is being loaded from disk (first use after download). */
    object ModelLoading : ChatUiState()

    /** Model is ready; the user can type questions. */
    object Ready : ChatUiState()

    /** A response is currently streaming token-by-token. */
    object Generating : ChatUiState()

    /** A non-recoverable error occurred; [message] contains details. */
    data class Error(val message: String) : ChatUiState()
}

/**
 * State for the model setup / download flow.
 */
sealed class ModelSetupState {
    object Idle : ModelSetupState()
    data class Downloading(val progress: Int) : ModelSetupState()
    object InstallComplete : ModelSetupState()
    data class Failed(val cause: String) : ModelSetupState()
}
