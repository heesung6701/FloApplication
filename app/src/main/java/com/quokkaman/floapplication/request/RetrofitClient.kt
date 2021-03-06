package com.quokkaman.floapplication.request

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val gson = GsonBuilder().setLenient().create()

    private const val BASE_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com"

    val instance : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}