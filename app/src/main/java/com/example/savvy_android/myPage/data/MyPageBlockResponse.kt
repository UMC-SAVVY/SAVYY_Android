package com.example.savvy_android.myPage.data

data class MyPageBlockResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<MyPageBlockResult>
)

data class MyPageBlockResult(
    val blocked_user: Int,
    val nickname: String,
)
