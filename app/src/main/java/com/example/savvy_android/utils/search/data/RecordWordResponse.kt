package com.example.savvy_android.utils.search.data

data class RecordWordResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<RecordWordResult>,
)
