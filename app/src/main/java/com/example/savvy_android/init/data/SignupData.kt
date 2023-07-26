package com.example.savvy_android.init.data

data class SignupRequest(
    val accessToken : String,
    val pic_url: String,
    val nickname: String,
    val intro: String
)

data class SignupResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: SignupResultData
)

data class SignupResultData(
    val token: String
)
