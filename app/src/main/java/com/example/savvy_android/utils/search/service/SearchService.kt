package com.example.savvy_android.utils.search.service

import com.example.savvy_android.utils.search.data.DeleteRecordResponse
import com.example.savvy_android.utils.search.data.WordRecordResponse
import com.example.savvy_android.utils.search.data.UserResponse
import com.example.savvy_android.home.data.HomeListResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface SearchService {
    @GET("searching/word")
    fun searchWord(
        @Header("Authorization") token: String,
        @Query("searchWord") word: String,
    ): Call<HomeListResponse>

    @GET("searching/word/list")
    fun recordWord(
        @Header("Authorization") token: String,
    ): Call<WordRecordResponse>

    @GET("searching/user")
    fun searchUser(
        @Header("Authorization") token: String,
        @Query("searchWord") word: String,
    ): Call<UserResponse>

    @GET("searching/user/list")
    fun recordUser(
        @Header("Authorization") token: String,
    ): Call<UserResponse>

    @DELETE("searching/delete")
    fun recordDelete(
        @Header("Authorization") token: String,
        @Query("searchWord") word: String,
        @Query("type") type: Int,
    ): Call<DeleteRecordResponse>

    @DELETE("searching/delete/all/{type}")
    fun recordDeleteAll(
        @Header("Authorization") token: String,
        @Path("type") type: Int,
    ): Call<DeleteRecordResponse>
}