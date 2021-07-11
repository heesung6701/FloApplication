package com.quokkaman.floapplication.request

import com.quokkaman.floapplication.dto.SongDTO
import io.reactivex.Single
import retrofit2.http.GET

interface RetrofitService {

    @GET("/2020-flo/song.json")
    fun getSong() : Single<SongDTO>
}