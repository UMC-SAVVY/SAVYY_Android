package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.comment.CommentCheckResponse
import com.example.savvy_android.diary.data.comment.CommentRequest
import com.example.savvy_android.diary.data.comment.CommentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

//댓글 작성
interface CommentService{
    @POST("comment/")
    fun commentMake(
        @Header("Authorization") token: String,
        @Body commentRequest: CommentRequest
    ): Call<CommentResponse>
}

