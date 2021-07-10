package com.quokkaman.floapplication.repository

import com.quokkaman.floapplication.dto.SongDTO
import com.quokkaman.floapplication.model.Song
import com.quokkaman.floapplication.network.RetrofitClient
import com.quokkaman.floapplication.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

object SongRepository {

    private val TAG = this::class.java.name

    private val service = RetrofitClient.instance.create(RetrofitService::class.java)

    fun getSong(onSuccess: (Song) -> Unit, onFail: (Throwable) -> Unit) {
        service.getSong().enqueue(object : Callback<SongDTO> {
            override fun onResponse(call: Call<SongDTO>, response: Response<SongDTO>) {
                val body = response.body()
                if (body == null) {
                    onFail(Exception("Body is Empty"))
                    return
                }
                val songDTO : SongDTO = body
                val song = songDTO.toModel()
                onSuccess(song)
                return
            }

            override fun onFailure(call: Call<SongDTO>, t: Throwable) {
                onFail(t)
                return
            }
        })
    }
}