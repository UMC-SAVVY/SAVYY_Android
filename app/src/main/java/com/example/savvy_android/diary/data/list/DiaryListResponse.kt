package com.example.savvy_android.diary.data.list

data class DiaryListResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<DiaryListResult>
)