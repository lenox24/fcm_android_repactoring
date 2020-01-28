package com.example.fcm_android.connector

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("/register")
    fun sendToken(@Query("id") id: String, @Query("name") name: String, @Query("deviceToken") deviceToken: String): Call<Void>

    @GET("/topic")
    fun topicNotification(@Query("title") title: String, @Query("body") body: String, @Query("topic") topic: String): Call<Void>

    @GET("/token")
    fun tokenNotification(@Query("title") title: String, @Query("body") body: String, @Query("id") id: String): Call<Void>

    @GET("/list")
    fun getTokenList(): Call<JsonArray>
}