package com.example.savvy_android.diary.data

data class CommentItemData(
    val position: Int,
    val userName: String,
    val commentContent: String,
    val date: String,
    val commentNum: Int,
    val nestedComment: MutableList<NestedCommentItemData>
)
