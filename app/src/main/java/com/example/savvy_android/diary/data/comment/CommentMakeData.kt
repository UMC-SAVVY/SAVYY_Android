package com.example.savvy_android.diary.data.comment

// 댓글 작성 Data
data class CommentRequest(
    val diary_id : Int,
    val content : String
)

data class CommentResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String
)


// 답글 작성 Data
data class NestedCommentRequest(
    val comment_id : Int?,
    val content : String
)

data class NestedCommentResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String
)
