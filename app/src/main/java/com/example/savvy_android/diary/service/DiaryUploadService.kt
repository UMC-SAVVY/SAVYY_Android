package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.DiaryUploadRequest
import com.example.savvy_android.diary.data.DiaryUploadResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DiaryUploadService {
    @POST("planner/upload")
    fun planUpload(
        @Header("Authorization") token: String,
        @Body diaryUploadRequest: DiaryUploadRequest
    ): Call<DiaryUploadResponse>
}

