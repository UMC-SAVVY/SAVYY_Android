package com.example.savvy_android.myPage.service

import com.example.savvy_android.myPage.data.BlockReleaseResponse
import com.example.savvy_android.myPage.data.MyPageBlockResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BlockService {
    @GET("user/block")
    fun blockList(
        @Header("Authorization") token: String,
    ): Call<MyPageBlockResponse>

    @DELETE("user/block")
    fun blockRelease(
        @Header("Authorization") token: String,
        @Query("blockedUser") userId: Int,
    ): Call<BlockReleaseResponse>
}