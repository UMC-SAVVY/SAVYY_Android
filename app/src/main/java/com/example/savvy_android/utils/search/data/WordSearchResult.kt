package com.example.savvy_android.utils.search.data

import com.example.savvy_android.diary.data.detail.DiaryHashtag

data class WordSearchResult(
    val id: Int,
    val title: String,
    val nickname: String,
    val updated_at: String,
    val likes_count: Int,
    val comments_count: Int,
    val thumbnail: String?,
    val img_count: Int,
    var hashtag: ArrayList<DiaryHashtag>?,
)
