package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.detail.DiaryDetailResponse
import com.example.savvy_android.diary.data.list.DiaryListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryService {
    @GET("")
    fun diaryListAll(
        @Header("Authorization") token: String,
    ): Call<DiaryListResponse>

    @GET("diary/list/mydiary")
    fun diaryListMine(
        @Header("Authorization") token: String,
    ): Call<DiaryListResponse>

    @GET("planner/search")
    fun diaryListSearch(
        @Header("Authorization") token: String,
        @Query("searchWord") word: String,
    ): Call<DiaryListResponse>

    @GET("diary/{diary_id}")
    fun diaryDetail(
        @Header("Authorization") token: String,
        @Path("diary_id") diaryId: String
    ):Call<DiaryDetailResponse>
}