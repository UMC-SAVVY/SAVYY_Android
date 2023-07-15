package com.example.savvy_android.diary.data

data class DiaryItemData(
    val id: Int,
    var title: String,
    var date: String,
    var like:Int,
    var comment:Int,
    var photoCount:Int,
    var photoUrl:Int,
    var isShow:Boolean,
)
