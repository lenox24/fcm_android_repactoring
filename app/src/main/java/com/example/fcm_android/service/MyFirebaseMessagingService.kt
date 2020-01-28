package com.example.fcm_android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fcm_android.R
import com.example.fcm_android.ui.MainActivity
import com.example.fcm_android.util.App
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        App.prefs.myToken = p0
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        val messageTitle: String
        val messageBody: String

        if (p0.notification != null) {
            messageTitle = p0.notification!!.title.toString()
            messageBody = p0.notification!!.body.toString()
        } else {
            messageTitle = p0.data["title"].toString()
            messageBody = p0.data["body"].toString()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(this, "ddd")
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "asdf"
            val channel =
                NotificationChannel("ddd", channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }


}