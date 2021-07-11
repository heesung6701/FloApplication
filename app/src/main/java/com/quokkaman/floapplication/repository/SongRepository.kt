package com.quokkaman.floapplication.repository

import com.quokkaman.floapplication.request.RetrofitClient
import com.quokkaman.floapplication.request.RetrofitService

object SongRepository {

    private val TAG = this::class.java.name

    private val service = RetrofitClient.instance.create(RetrofitService::class.java)

    fun getSong() = service.getSong()
}