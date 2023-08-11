package com.example.savvy_android.home.data

data class HomeListResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<HomeListResult>,
)
