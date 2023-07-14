package com.example.savvy_android.data


data class DiaryViewItemData(
    val position: Int,
    var isText: Boolean,
    var text: String?,
    var Image: Int?,
    var hasPlace: Boolean,
    var placeName: String,
    var placeUrl: String?,
)
