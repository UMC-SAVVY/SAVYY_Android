package com.example.savvy_android.myPage.data

import com.example.savvy_android.diary.data.list.DiaryListResult

data class UserDiaryResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: Diary,
)

data class Diary(
    val diary: List<DiaryListResult>,
    val amount_diary : Int,
)