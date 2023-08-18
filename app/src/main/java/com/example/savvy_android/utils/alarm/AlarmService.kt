package com.example.savvy_android.utils.alarm

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface AlarmService {
    @GET("user/alarm/list")
    fun alarmList(
        @Header("Authorization") token: String,
    ): Call<AlarmResponse>
}