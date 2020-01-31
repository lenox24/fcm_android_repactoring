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
    // 새로운 토큰이 생성되었을 경우에 실행되는 함수
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        //SharedPreferences 에 token을 저장
        App.prefs.myToken = token
    }

    // 앱이 foreground 상황에서 알림이 온 경우
    override fun onMessageReceived(message: RemoteMessage) {
        val messageTitle: String
        val messageBody: String


        if (message.notification != null) { // 토큰 알림이 온 경우
            messageTitle = message.notification!!.title.toString()
            messageBody = message.notification!!.body.toString()
        } else {                            // 토픽 알림이 온 경우
            messageTitle = message.data["title"].toString()
            messageBody = message.data["body"].toString()
        }

        // 알림을 눌렀을 경우에 메인엑티비티를 띄우기 위한 인텐트
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // 펜딩 인텐트를 통해 알림을 눌렀을 경우에 인텐트를 실행
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // 알림 내용을 가지고 디바이스에 띄워줄 알림 생성
        val notificationBuilder = NotificationCompat.Builder(this, "ddd")
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // 알림 관리자 설정
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 버전이 일정 버전 이상인 경우 예외처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("testId", "testChannel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 띄우기
        notificationManager.notify(0, notificationBuilder.build())
    }
}