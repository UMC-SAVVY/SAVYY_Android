package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.comment.NestedCommentRequest
import com.example.savvy_android.diary.data.comment.NestedCommentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

//답글 작성
interface NestedCommentService{
    @POST("comment/reply")
    fun nestedCommentMake(
        @Header("Authorization") token: String,
        @Body nestedCommentRequest: NestedCommentRequest
    ): Call<NestedCommentResponse>
}
