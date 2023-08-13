package com.example.savvy_android.diary.data

import com.example.savvy_android.plan.data.Timetable


data class DiaryUploadRequest(
    val id: Int?,
    val memo: String,
    val nickname: String,
    val timetable: MutableList<Timetable>,
    val title: String,
    val updated_at: String
)

data class DiaryUploadResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Result
)

data class Result(
    val planner_id : Int
)
