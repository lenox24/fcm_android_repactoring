package com.example.fcm_android.connector

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Connector {
    private var retrofit: Retrofit
    private var api: API
    private const val url = "https://6bee7c51.ngrok.io"

    init {
        // http 인터셉터 설정
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // okhttp 클라이언트 빌더
        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .build()

        // retrofit 빌더
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // api 연결
        api = retrofit.create(API::class.java)
    }

    // 외부에서 api에 접속하기 위한 함수
    fun createApi(): API = retrofit.create(API::class.java)
}