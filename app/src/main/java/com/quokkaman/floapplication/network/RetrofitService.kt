package com.quokkaman.floapplication.network

import com.quokkaman.floapplication.dto.SongDTO
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("/2020-flo/song.json")
    fun getSong() : Call<SongDTO>
}