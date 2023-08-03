package com.example.savvy_android.utils.report

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ReportService {
    @POST("planner/report")
    fun report(
        @Header("Authorization") token: String,
        @Body reportRequest: ReportRequest
    ): Call<ReportResponse>

}
