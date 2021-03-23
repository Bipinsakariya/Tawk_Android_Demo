package com.kotlin.androidtest.Service

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.kotlin.androidtest.utlis.Glob
import okhttp3.Interceptor
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SellerServices {

    object list {
        val listinterface: BackendService

        private lateinit var okHttpClientBuilder: okhttp3.OkHttpClient.Builder

        init {
            okHttpClientBuilder = okhttp3.OkHttpClient.Builder()
            okHttpClientBuilder.addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val newRequest = request.newBuilder()
                chain.proceed(newRequest.build())
            })

            val gson = GsonBuilder()
                .setLenient()
                .create()

            okHttpClientBuilder
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(ServiceConstants().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClientBuilder.build())
                .build()

            listinterface = retrofit.create(BackendService::class.java)

        }
    }
}