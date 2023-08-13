package com.example.savvy_android.plan.data

data class PlanDetailResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Result,
)

data class Result(
    val id: Int,
    val memo: String,
    val nickname: String,
    val pic_url: String?,
    val title: String,
    val updated_at: String,
    val timetable: MutableList<Timetable>,
)

