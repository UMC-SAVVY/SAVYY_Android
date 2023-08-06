package com.example.savvy_android.utils.search.data

data class WordRecordResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<WordRecordResult>,
)
