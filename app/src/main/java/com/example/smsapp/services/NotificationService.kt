package com.example.smsapp.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.example.smsapp.MainActivity
import com.example.smsapp.R
import com.example.smsapp.models.Conversation

class NotificationService(private val context: Context) {

    companion object { const val CHANNEL_ID = "sms_notification_channel" }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init { createNotificationChannel() }

    private fun createNotificationChannel() {
        val name = "SMS Notification Channel"
        val descriptionText = "Channel for income sms notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("RestrictedApi")
    fun notifyMessage(address: String, body: String, conversation: Conversation) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", conversation)
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val icon = IconCompat.createFromIcon(android.graphics.drawable.Icon.createWithResource(context, R.drawable.arrow_down))
        val user = Person.Builder().setName(address).setIcon(icon).build()
        val msg = NotificationCompat.MessagingStyle.Message(body, System.currentTimeMillis(), user)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.message)
        .setStyle(NotificationCompat.MessagingStyle(user).addMessage(msg))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

        this.notify(0, notification)
    }

    fun notify(id: Int, notification: Notification?) {

        if( notification == null ||
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) { return }

        notificationManager.notify(id, notification)
//        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
}