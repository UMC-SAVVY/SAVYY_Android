package com.example.savvy_android.myPage.service

import com.example.savvy_android.myPage.data.MyPageBlockResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface BlockService {
    @GET("user/block")
    fun blockList(
        @Header("Authorization") token: String,
    ): Call<MyPageBlockResponse>
}