package com.example.savvy_android.init.data.autoLogin

data class AutoLoginResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: AutoLoginResult,
)
