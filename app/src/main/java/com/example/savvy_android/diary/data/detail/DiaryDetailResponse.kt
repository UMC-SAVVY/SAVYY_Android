package com.example.savvy_android.diary.data.detail

data class DiaryDetailResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: DiaryDetailResult
)