package com.example.fcm_android.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.fcm_android.R
import com.example.fcm_android.connector.Connector
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    // Topic과 Token을 저장해놓기 위한 List
    companion object {
        private val topics = ArrayList<String>()
        private val tokens = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase 인스턴스 생성 및 기본 데이터 추가
        dataInit()

        // Topic 알림과 Token 알림을 구분하기 위한 Boolean 변수
        var flag = true

        // Topic과 Token List를 보여줄 리스트 뷰 어뎁터
        var adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, topics)
        listView.adapter = adapter

        // Topic 구독과 Token을 서버에 보내기 위한 곳으로 이동하기 위한 버튼
        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 알림 전송 버튼을 눌렀을 경우
        btn_notification.setOnClickListener {
            // editText에서 text를 가져오고 listView에서 선택된 곳에 보내기 위해 가져오기.
            val title = edt_title.text.toString()
            val body = edt_body.text.toString()
            val target = listView.selectedItem.toString()

            if (flag) {
                // Topic 알림일 경우
                // Retrofit2 을 이용해 알림 보내기
                Connector.createApi().topicNotification(title, body, target)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            Log.d("topicNotification-ResponseBody", "${response.code()}")
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d("topicNotification-Failure-Throwable", "$t")
                        }
                    })
            } else {
                // Token 알림일 경우
                // Retrofit2 을 이용해 알림 보내기
                Connector.createApi().tokenNotification(title, body, target)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            Log.d("tokenNotification-ResponseBody", "${response.code()}")
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d("tokenNotification-Failure-Throwable", "$t")
                        }
                    })
            }
        }

        // Token 알림과 Topic 알림을 구분하기 위한 라디오 버튼 클릭 이벤트 리스너
        radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_topic -> {   // Topic 알림을 선택한 경우
                    flag = true // Boolean 변수를 true로 바꾸고
                    adapter =   // List는 Topic List로 변경
                        ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, topics)
                    listView.adapter = adapter
                }
                R.id.radio_token -> {   // Token 알림을 선택한 경우
                    flag = false //Boolean 변수를 false로 바꾸고
                    adapter =    // List는 Token List로 변경
                        ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, tokens)
                    listView.adapter = adapter
                }
            }
        }
    }

    // 기본 정보 입력
    private fun dataInit() {
        // Firebase 인스턴스 생성
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (!it.isSuccessful) { // 실패 시 예외 처리
                    Log.d("FCM Log", "getInstanceId failed", it.exception)
                    return@addOnCompleteListener
                }
            }

        // 기본 Topic 값 설정
        topics.add("A")
        topics.add("B")
        topics.add("C")
        topics.add("NewNotice")
        topics.add("NewArticle")
        topics.add("NewComment")

        // 서버로부터 Token List 받아오기
        getTokens()
    }

    // 서버로 부터 Token List 받아오기
    private fun getTokens() {
        Connector.createApi().getTokenList().enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d("getToken-Failure-Throwable", "$t")
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                // 성공한 경우
                if (response.code() == 200) {
                    Log.d("getToken-ResponseBody", "${response.body()}")
                    val obj = response.body() as JsonArray

                    // tokens(리스트)에 token의 ID를 하나씩 넣어주기
                    obj.forEach {
                        val tmp = it.asJsonObject
                        tokens.add(tmp.get("ID").asString)
                        Log.d("addTokens-Element", tmp["ID"].asString)
                    }
                }
            }
        })
    }
}

/*
  val option = FirebaseOptions.Builder()
            .setApplicationId("1:217163938319:android:e4829086ec5d2c3f58c17a")
            .setApiKey("AIzaSyAV294VFXC9NtunZfN4uPWsyU1Q-9CF2bo")
            .build()

        val option2 = FirebaseOptions.Builder()
            .setApplicationId("1:674747678941:android:973f2c855efd01a7b51b34")
            .setApiKey("AIzaSyBBuiblc2x_aT3IMEZ24IkLwBXETVQ36lg")
            .build()

        val app = FirebaseApp.initializeApp(this, option, "TestT")

        val app2 = FirebaseApp.initializeApp(this, option2, "TestE")

        FirebaseInstanceId.getInstance(app).instanceId.addOnCompleteListener {
            if (!it.isSuccessful) { // 실패 시 예외 처리
                Log.d("FCM Log", "getInstanceId failed", it.exception)
                return@addOnCompleteListener
            }
            Log.d("FCM Token", FirebaseInstanceId.getInstance(app).getToken())
        }
        FirebaseInstanceId.getInstance(app2).instanceId.addOnCompleteListener {
            if (!it.isSuccessful) { // 실패 시 예외 처리
                Log.d("FCM Log", "getInstanceId failed", it.exception)
                return@addOnCompleteListener
            }
            Log.d("FCM Token", FirebaseInstanceId.getInstance(app2).getToken())
        }

        app.get(FirebaseMessaging::class.java).subscribeToTopic("p0").addOnCompleteListener {
            if (!it.isSuccessful)
                Log.d("Error", it.exception.toString())
        }

       /* app2.get(FirebaseMessaging::class.java).subscribeToTopic("p1999").addOnCompleteListener {
            if (!it.isSuccessful)
                Log.d("Error", it.exception.toString())
        }*/
 */