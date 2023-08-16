package com.example.savvy_android.myPage.service

import com.example.savvy_android.myPage.data.UserDiaryResponse
import com.example.savvy_android.myPage.data.UserPageResponse
import com.example.savvy_android.myPage.data.UserPlannerResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MyPageService {
    @GET("user/mypage")
    fun myPageInfo(
        @Header("Authorization") token: String,
    ): Call<UserPageResponse>

    @GET("user/mypage/diary")
    fun myPageDiary(
        @Header("Authorization") token: String,
    ): Call<UserDiaryResponse>

    @GET("user/mypage/planner")
    fun myPagePlanner(
        @Header("Authorization") token: String,
    ): Call<UserPlannerResponse>

    @GET("user/others")
    fun otherPageInfo(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("searching") isSearching: Boolean,
    ): Call<UserPageResponse>

    @GET("user/others/diary")
    fun otherPageDiary(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
    ): Call<UserDiaryResponse>

    @GET("user/others/planner")
    fun otherPagePlanner(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
    ): Call<UserPlannerResponse>
}