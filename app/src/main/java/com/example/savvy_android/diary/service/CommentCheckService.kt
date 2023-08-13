package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.comment.CommentCheckResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CommentCheckService{
    @GET("comment/{diary_id}")
    fun commentCheck(
        @Header("Authorization") token: String,
        @Path("diary_id") diaryID: String
    ): Call<CommentCheckResponse>
}
