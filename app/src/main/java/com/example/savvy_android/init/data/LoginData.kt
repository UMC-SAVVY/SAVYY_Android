package com.example.savvy_android.init.data

data class LoginRequest(
    val kakaoToken : String
)

data class LoginResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: LoginResultData
)

data class LoginResultData(
    val token: String,
    val nickname:String
)
