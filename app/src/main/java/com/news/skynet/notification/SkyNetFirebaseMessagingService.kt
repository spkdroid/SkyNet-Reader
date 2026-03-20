package com.news.skynet.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.news.skynet.MainActivity
import com.news.skynet.R

/**
 * Handles incoming FCM push notifications — both data and notification messages.
 *
 * Notification channels (Android 8.0+):
 *  - **breaking_news** — high-priority alerts for breaking stories
 *  - **daily_digest**  — low-priority daily summary notifications
 */
class SkyNetFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onNewToken(token: String) {
        // Send the new FCM registration token to your backend if needed
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val title = data["title"] ?: message.notification?.title ?: return
        val body = data["body"] ?: message.notification?.body.orEmpty()
        val articleUrl = data["article_url"]
        val channel = data["channel"] ?: CHANNEL_BREAKING

        showNotification(title, body, articleUrl, channel)
    }

    private fun showNotification(
        title: String,
        body: String,
        articleUrl: String?,
        channelId: String
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (articleUrl != null) {
                data = "skynet://article?url=$articleUrl".toUri()
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(
                if (channelId == CHANNEL_BREAKING) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val breaking = NotificationChannel(
                CHANNEL_BREAKING,
                "Breaking News",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Urgent breaking news alerts" }

            val digest = NotificationChannel(
                CHANNEL_DIGEST,
                "Daily Digest",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Daily news summary" }

            manager.createNotificationChannels(listOf(breaking, digest))
        }
    }

    companion object {
        const val CHANNEL_BREAKING = "breaking_news"
        const val CHANNEL_DIGEST = "daily_digest"
    }
}
