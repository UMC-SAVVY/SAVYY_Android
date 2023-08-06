package com.example.savvy_android.utils.search.data

data class UserResult(
    val id: Int,
    val nickname: String,
    val pic_url: String?,
    val likes: Int,
    val amount_planner: Int,
    val amount_diary: Int,
    val searching_at: String,
)
