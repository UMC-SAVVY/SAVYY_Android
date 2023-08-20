package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.comment.CommentModifyRequest
import com.example.savvy_android.diary.data.comment.CommentModifyResponse
import com.example.savvy_android.diary.data.comment.NestedCommentModifyRequest
import com.example.savvy_android.diary.data.comment.NestedCommentModifyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface CommentModifyService {
    @PUT("comment")
    fun commentModify(
        @Header("Authorization") token: String,
        @Body commentModifyRequest: CommentModifyRequest
    ): Call<CommentModifyResponse>
}


interface NestedCommentModifyService {
    @PUT("comment/reply")
    fun nestedCommentModify(
        @Header("Authorization") token: String,
        @Body nestedCommentModifyRequest: NestedCommentModifyRequest
    ): Call<NestedCommentModifyResponse>
}

