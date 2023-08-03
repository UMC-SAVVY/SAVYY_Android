package com.example.savvy_android.diary.data.detail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiaryContent(
    val count: Int,
    val type: String,
    var content: String,
    var location: String?,
) : Parcelable