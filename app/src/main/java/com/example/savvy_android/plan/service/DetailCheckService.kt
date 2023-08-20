package com.example.savvy_android.plan.service

import com.example.savvy_android.plan.data.PlanCheckRequest
import com.example.savvy_android.plan.data.PlanCheckResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface DetailCheckService {
    @PUT("planner/checklist")
    fun detailCheck(
        @Header("Authorization") token: String,
        @Body planCheckRequest: PlanCheckRequest
    ): Call<PlanCheckResponse>
}