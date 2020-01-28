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
    private val topics = ArrayList<String>()
    private val tokens = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dataInit()

        var flag = true
        var adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, topics)
        listView.adapter = adapter

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btn_notification.setOnClickListener {
            val title = edt_title.text.toString()
            val body = edt_body.text.toString()
            val target = listView.selectedItem.toString()

            if (flag) {
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

        radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_topic -> {
                    flag = true
                    adapter =
                        ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, topics)
                    listView.adapter = adapter
                }
                R.id.radio_token -> {
                    flag = false
                    adapter =
                        ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, tokens)
                    listView.adapter = adapter
                }
            }
        }
    }

    private fun dataInit() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.d("FCM Log", "getInstanceId failed", it.exception)
                    return@addOnCompleteListener
                }
                val token = it.result!!.token
                Log.d("FCM Log", "FCM 토큰: $token")
            }

        topics.add("A")
        topics.add("B")
        topics.add("C")

        getTokens()
    }

    private fun getTokens() {
        Connector.createApi().getTokenList().enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d("getToken-Failure-Throwable", "$t")
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.code() == 200) {
                    Log.d("getToken-ResponseBody", "${response.body()}")
                    val obj = response.body() as JsonArray

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
