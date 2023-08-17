package com.example.savvy_android.diary.data.comment

// 댓글 작성 Data
data class CommentModifyRequest(
    val comment_id : Int,
    val content : String
)

data class CommentModifyResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String
)


// 답글 작성 Data
data class NestedCommentModifyRequest(
    val reply_id : Int,
    val content : String
)

data class NestedCommentModifyResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String
)
