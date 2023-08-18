package com.example.savvy_android.utils.alarm

data class AlarmResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<AlarmResult>,
)

data class AlarmResult(
    val user_id: Int,
    val nickname: String,
    val type: String,
    val updated_at: String,
    var read_status: Int,
    val body: String,
)