package com.example.savvy_android.diary.data.list

data class DiaryListResult(
    val id: Int,
    val title: String,
    val updated_at: String,
    val likes_count: Int,
    val comments_count: Int,
    val thumbnail: String?,
    val img_count: Int,
    var is_public: Boolean,
)