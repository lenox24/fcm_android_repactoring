package com.example.fcm_android.util

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    private val tokenKey = "token"
    private val prefs: SharedPreferences = context.getSharedPreferences(tokenKey, 0)

    // 토큰을 저장할 문자열 변수
    var myToken: String
        get() = prefs.getString(tokenKey, "").toString()
        set(value) = prefs.edit().putString(tokenKey, value).apply()

    // A 구독 상태를 저장할 문자열 변수
    var subA: Boolean
        get() = prefs.getBoolean("A", false)
        set(value) = prefs.edit().putBoolean("A", value).apply()

    // B 구독 상태를 저장할 문자열 변수
    var subB: Boolean
        get() = prefs.getBoolean("B", false)
        set(value) = prefs.edit().putBoolean("B", value).apply()

    // C 구독 상태를 저장할 문자열 변수
    var subC: Boolean
        get() = prefs.getBoolean("C", false)
        set(value) = prefs.edit().putBoolean("C", value).apply()

    // 회원가입 여부를 저장할 문자열 변수
    var register: Boolean
        get() = prefs.getBoolean("Register", false)
        set(value) = prefs.edit().putBoolean("Register", value).apply()
}