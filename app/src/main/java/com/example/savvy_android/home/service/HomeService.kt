package com.example.savvy_android.home.service

import com.example.savvy_android.home.data.HomeListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HomeService {
    @GET("diary/home")
    fun homeListFirst(
        @Header("Authorization") token: String,
    ): Call<HomeListResponse>

    @GET("diary/home")
    fun homePaging(
        @Header("Authorization") token: String,
        @Query("diary_id") diaryId : Int
    ): Call<HomeListResponse>
}