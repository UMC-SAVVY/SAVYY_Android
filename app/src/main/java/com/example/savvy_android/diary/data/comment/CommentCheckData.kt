package com.example.savvy_android.diary.data.comment

//댓글, 답글 조회 Data
data class CommentCheckResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String,
    val result : MutableList<CommentResult>
)

data class CommentResult(
    val id : Int,
    val nickname : String,
    val pic_url : String,
    val content : String,
    val updated_at : String,
    val is_updated : Boolean,
    val reply_count : String,
    var reply_List : MutableList<NestedCommentResult>
)
data class NestedCommentResult(
    val id : Int,
    val nickname : String,
    val pic_url : String,
    val content : String,
    val updated_at : String,
    val is_updated : Boolean,
)

