package com.example.savvy_android.diary.data.comment

//댓글 조회 Data
data class CommentCheckResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String,
    val result : MutableList<Result>
)

data class Result(
    val id : Int,
    val nickname : String,
    val pic_url : String,
    val content : String,
    val updated_at : String,
    val is_updated : Boolean
)


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

