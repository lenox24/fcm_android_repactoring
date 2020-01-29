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
    companion object {
        private const val subT = "구독 중"
        private const val subF = "구독"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initialize()

        btn_register.setOnClickListener {
            val id = edt_id.text.toString()
            val name = edt_name.text.toString()

            App.prefs.register = true
            btn_register.isEnabled = false
            sendToken(id, name)
        }

        val subscribeListener = View.OnClickListener {
            when (it.id) {
                R.id.btn_a -> {
                    App.prefs.subA = onButton(btn_a, "A")
                }
                R.id.btn_b -> {
                    App.prefs.subB = onButton(btn_b, "B")
                }
                R.id.btn_c -> {
                    App.prefs.subC = onButton(btn_c, "C")
                }
            }
        }

        btn_a.setOnClickListener(subscribeListener)
        btn_b.setOnClickListener(subscribeListener)
        btn_c.setOnClickListener(subscribeListener)
    }

    private fun initialize() {
        if (App.prefs.subA)
            btn_a.text = subT
        if (App.prefs.subB)
            btn_b.text = subT
        if (App.prefs.subC)
            btn_c.text = subT
        if (App.prefs.register)
            btn_register.isEnabled = false
    }

    private fun sendToken(id: String, name: String) {
        val deviceToken: String = App.prefs.myToken
        if (deviceToken != "") {
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

    private fun onSubscribe(topic: String, subscribe: String) {
        val messaging = FirebaseMessaging.getInstance()
        when (subscribe) {
            "sub" -> {
                messaging.subscribeToTopic(topic).addOnCompleteListener {
                    if (!it.isSuccessful)
                        Log.d("subscribeToTopic-Failure", "Subscribe Failed: $topic")
                    else Log.d("subscribeToTopic-Successful", "Subscribe Complete: $topic")
                }
            }
            "unsub" -> {
                messaging.unsubscribeFromTopic(topic).addOnCompleteListener {
                    if (!it.isSuccessful)
                        Log.d("unsubscribeToTopic-Failure", "Subscribe Failed: $topic")
                    else Log.d("unsubscribeToTopic-Successful", "Subscribe Complete: $topic")
                }
            }
        }
    }

    private fun onButton(btn: Button, topic: String): Boolean {
        when (btn.text.toString()) {
            subF -> {
                btn.text = subT
                onSubscribe(topic, "sub")
                return true
            }
            subT -> {
                btn.text = subF
                onSubscribe(topic, "unsub")
                return false
            }
        }
        return false
    }
}
