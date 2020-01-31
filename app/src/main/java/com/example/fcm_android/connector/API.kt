package com.example.fcm_android.connector

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("/register")   // token을 서버에 보내기 위한 API
    fun sendToken(@Query("id") id: String, @Query("name") name: String, @Query("deviceToken") deviceToken: String): Call<Void>

    @GET("/topic")  // topic을 이용한 알림을 보내는 API
    fun topicNotification(@Query("title") title: String, @Query("body") body: String, @Query("topic") topic: String): Call<Void>

    @GET("/token")  // token을 이용한 알림을 보내는 API
    fun tokenNotification(@Query("title") title: String, @Query("body") body: String, @Query("id") id: String): Call<Void>

    @GET("/list")   // token list를 받아오는 API
    fun getTokenList(): Call<JsonArray>
}