package com.example.savvy_android.diary.data.make_modify

import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryHashtag
import java.io.Serializable

data class DiaryMakeRequest(
    val title: String,
    val planner_id: Int?,
    val is_public: Boolean,
    val is_temporary: Boolean,
    val content: ArrayList<DiaryContent>?,
    val hashtag: ArrayList<DiaryHashtag>?,
) : Serializable