package com.example.savvy_android.diary.data.detail

data class DiaryDetailResult(
    val user_id: Int,
    val nickname: String,
    val pic_url: String?, // user profile image
    val updated_at: String, // diary update date
    val comments_count: Int,
    val likes_count: Int,
    val planner_id: Int?,
    val title: String,
    val isLiked : Boolean,
    val content: ArrayList<DiaryContent>?,
    val hashtag: ArrayList<DiaryHashtag>?,
)