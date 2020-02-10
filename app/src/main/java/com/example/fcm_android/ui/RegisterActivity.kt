package com.example.fcm_android.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.fcm_android.R
import com.example.fcm_android.connector.Connector
import com.example.fcm_android.util.App
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    // 상태를 나타내는 문자열 상수 선언
    companion object {
        private const val subT = "구독 중"
        private const val subF = "구독"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 초기 설정
        initialize()

        // 회원가입 버튼을 누른 경우
        btn_register.setOnClickListener {
            // id와 이름을 받아온다
            val id = edt_id.text.toString()
            val name = edt_name.text.toString()

            // 그리고 앱을 지울때까지 토큰이 변경될 일은 없기 때문에 버튼을 비활성화 해준다.
            App.prefs.register = true
            btn_register.isEnabled = false

            // 토큰을 서버에 보낸다
            sendToken(id, name)
        }

        // 구독 버튼을 눌렀을 경우에 해당하는 리스너 생성
        val subscribeListener = View.OnClickListener {
            when (it.id) {
                R.id.btn_a -> { // A를 구독한 경우
                    App.prefs.subA = onButton(btn_a, "A")
                }
                R.id.btn_b -> { // B를 구독한 경우
                    App.prefs.subB = onButton(btn_b, "B")
                }
                R.id.btn_c -> { // C를 구독한 경우
                    App.prefs.subC = onButton(btn_c, "C")
                }
            }
        }
        // 각 버튼에 만들어진 리스너 할당
        btn_a.setOnClickListener(subscribeListener)
        btn_b.setOnClickListener(subscribeListener)
        btn_c.setOnClickListener(subscribeListener)

        btn_notice.setOnCheckedChangeListener { _, isChecked ->
            App.prefs.subNotice = onSwitch("NewNotice", isChecked)
        }
        btn_article.setOnCheckedChangeListener { _, isChecked ->
            App.prefs.subArticle = onSwitch("NewArticle", isChecked)
        }
        btn_comments.setOnCheckedChangeListener { _, isChecked ->
            App.prefs.subComment = onSwitch("NewComment", isChecked)
        }
    }

    private fun initialize() {
        // 저장된 상태에 따라 버튼의 문구와 활성화 여부를  변경
        if (App.prefs.subA)
            btn_a.text = subT
        if (App.prefs.subB)
            btn_b.text = subT
        if (App.prefs.subC)
            btn_c.text = subT
        if (App.prefs.register)
            btn_register.isEnabled = false

        btn_notice.isChecked = !App.prefs.subNotice
        btn_article.isChecked = !App.prefs.subArticle
        btn_comments.isChecked = !App.prefs.subComment
    }

    private fun sendToken(id: String, name: String) {
        // SharedPreference 에 저장된 Token 의 값을 불러와서 저장
        val deviceToken: String = App.prefs.myToken

        if (deviceToken != "") {    // 저장된 Token 이 있는 경우
            Connector.createApi().sendToken(id, name, deviceToken).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("sendToken-Failure-Throwable", "$t")
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("sendToken-ResponseBody", "${response.code()}")
                }

            })
        }
    }

    private fun onSwitch(topic: String, isChecked: Boolean): Boolean {
        return when (isChecked) {
            true -> {
                onSubscribe(topic, "sub")
                false
            }
            false -> {
                onSubscribe(topic, "unsub")
                true
            }
        }
    }

    private fun onButton(btn: Button, topic: String): Boolean {
        // 구독 또는 비구독을 누른 버튼에 따라서
        when (btn.text.toString()) {
            subF -> {   // 비구독 상태에서 누른 경우
                btn.text = subT // 문구를 구독으로 변경
                onSubscribe(topic, "sub")   // 구독
                return true // SharedPreference 에 저장할 상태값 반환
            }
            subT -> {   // 구독 상태에서 누른 경우
                btn.text = subF // 문구를 비구독으로 변경
                onSubscribe(topic, "unsub") // 구독 해제
                return false    // SharedPreference 에 저장할 상태값 반환
            }
        }
        return false // SharedPreference 에 저장할 상태값 반환 (해당하는 게 없는 경우 기본적으로 false를 반환
    }

    // 구독 함수
    private fun onSubscribe(topic: String, subscribe: String) {
        // 인스턴스 불러오기
        val messaging = FirebaseMessaging.getInstance()
        when (subscribe) {  // 요청한 구독 상태에 따라서 분리
            "sub" -> {  // 구독 요청일 경우
                messaging.subscribeToTopic(topic).addOnCompleteListener {
                    if (!it.isSuccessful)
                        Log.d("subscribeToTopic-Failure", "Subscribe Failed: $topic")
                    else Log.d("subscribeToTopic-Successful", "Subscribe Complete: $topic")
                }
            }
            "unsub" -> {    // 구독 해제 요청일 경우
                messaging.unsubscribeFromTopic(topic).addOnCompleteListener {
                    if (!it.isSuccessful)
                        Log.d("unsubscribeToTopic-Failure", "Subscribe Failed: $topic")
                    else Log.d("unsubscribeToTopic-Successful", "Subscribe Complete: $topic")
                }
            }
        }
    }
}
