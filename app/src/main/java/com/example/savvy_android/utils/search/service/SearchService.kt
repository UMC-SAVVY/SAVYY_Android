package com.example.savvy_android.utils.search.service

import com.example.savvy_android.utils.search.data.RecordWordResponse
import com.example.savvy_android.utils.search.data.RecordUserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface SearchService {
    @GET("searching/word/list")
    fun recordWord(
        @Header("Authorization") token: String,
    ): Call<RecordWordResponse>

    @GET("searching/user/list")
    fun recordUser(
        @Header("Authorization") token: String,
    ): Call<RecordUserResponse>
}