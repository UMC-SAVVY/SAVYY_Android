package com.example.savvy_android.diary.service

import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentDeleteService {
    @DELETE("comment/{comment_id}")
    fun commentDelete(
        @Header("Authorization") token: String,
        @Path("comment_id") commentId: String,
    ): Call<ServerDefaultResponse>
}

interface NestedCommentDeleteService {
    @DELETE("comment/reply/{reply_id}")
    fun nestedCommentDelete(
        @Header("Authorization") token: String,
        @Path("reply_id") replyId: String,
    ): Call<ServerDefaultResponse>
}