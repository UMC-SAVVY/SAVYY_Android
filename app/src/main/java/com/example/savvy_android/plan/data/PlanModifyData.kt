package com.example.savvy_android.plan.data

data class PlanModifyRequest(
    val id: Int,
    val memo: String,
    val nickname: String,
    val timetable: MutableList<Timetable>,
    val title: String,
    val updated_at: String
)

data class PlanModifyResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)
