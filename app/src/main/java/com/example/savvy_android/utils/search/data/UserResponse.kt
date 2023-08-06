package com.example.savvy_android.utils.search.data

data class UserResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<UserResult>,
)
