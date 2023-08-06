package com.example.savvy_android.utils.search.data

data class WordSearchResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<WordSearchResult>,
)
