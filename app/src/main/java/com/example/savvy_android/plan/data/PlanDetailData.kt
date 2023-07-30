package com.example.savvy_android.plan.data

class PlanDetailResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Result
)

data class Result(
    val id: Long?,
    val memo: String,
    val nickname: String,
    val title: String,
    val updated_at: String,
    val timetable: MutableList<Timetable>
)

