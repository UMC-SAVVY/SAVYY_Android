package com.example.savvy_android.diary.data.make_modify

import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryHashtag

data class DiaryModifyRequest(
    val title: String,
    val diary_id: Int,
    val content: ArrayList<DiaryContent>?,
    val hashtag: ArrayList<DiaryHashtag>?,
)