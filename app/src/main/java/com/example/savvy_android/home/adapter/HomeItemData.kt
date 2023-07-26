package com.example.savvy_android.home.adapter

data class HomeItemData(
    var title: String,
    val user: String,
    var date: String,
    var img: String?,
    var tag: String?,
    var likeCount: Int,
    var commentCount: Int,
)
