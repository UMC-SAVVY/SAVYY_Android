package com.example.savvy_android.diary.data


data class DiaryDetailItemData(
    val position: Int,
    var isText: Boolean,
    var text: String?,
    var Image: Int?,
    var hasPlace: Boolean?,
    var placeName: String?,
    var placeUrl: String?,
)
