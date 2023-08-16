package com.example.savvy_android.myPage.data

data class UserPageResult(
    val id: Int,
    val pic_url: String?,
    val nickname: String,
    val intro: String,
    val likes: Int,
    val amount_diary: Int,
    val amount_planner: Int,
)