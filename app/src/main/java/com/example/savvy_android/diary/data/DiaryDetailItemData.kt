package com.example.savvy_android.diary.data


data class DiaryDetailItemData(
    val position: Int,
    var isText: Boolean,
    var text: String?,
    var image: String?,
    var hasPlace: Boolean?,
    var placeName: String?,
    var placeUrl: String?,
)
