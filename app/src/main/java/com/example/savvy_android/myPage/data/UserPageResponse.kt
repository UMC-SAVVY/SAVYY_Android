package com.example.savvy_android.myPage.data

data class UserPageResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: UserPageResult
)