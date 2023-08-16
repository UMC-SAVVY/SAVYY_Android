package com.example.savvy_android.init.data.image

data class MultipleImageResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ArrayList<UploadDiaryImageResult>,
)