package com.example.savvy_android.diary.data.detail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiaryHashtag(
    var tag: String,
) : Parcelable