package com.example.savvy_android.diary.data.comment

// 답글 조회 Data
data class NestedCommentCheckResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String,
    val result : MutableList<NestedResult>
)

data class NestedResult(
    val id : Int,
    val nickname : String,
    val pic_url : String,
    val content : String,
    val updated_at : String,
    val is_updated : Boolean
)

// 답글 작성 Data
data class NestedCommentRequest(
    val comment_id : Int,
    val content : String
)

data class NestedCommentResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String
)
