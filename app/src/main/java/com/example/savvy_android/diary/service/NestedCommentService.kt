package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.comment.NestedCommentCheckResponse
import com.example.savvy_android.diary.data.comment.NestedCommentRequest
import com.example.savvy_android.diary.data.comment.NestedCommentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NestedCommentService{
    @POST("comment/reply")
    fun nestedCommentMake(
        @Header("Authorization") token: String,
        @Body nestedCommentRequest: NestedCommentRequest
    ): Call<NestedCommentResponse>
}

interface NestedCommentCheckService{
    @GET("comment/reply/{comment_id}")
    fun nestedCommentCheck(
        @Header("Authorization") token: String,
        @Path("comment_id") commentID: String
    ): Call<NestedCommentCheckResponse>
}
