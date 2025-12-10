package com.haris.semesterproject.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // IMPORTANT: REPLACE THIS URL DEPENDING ON WHERE YOU RUN THE APP
    const val BASE_URL = "http://192.168.137.1/semester_api/"

    val retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val gson = GsonBuilder()
            .setLenient() // allows malformed JSON
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            ))
            .client(client)
            .build()
    }


    // API implementation
    val api: Api by lazy {
        retrofit.create(Api::class.java)
    }
}
