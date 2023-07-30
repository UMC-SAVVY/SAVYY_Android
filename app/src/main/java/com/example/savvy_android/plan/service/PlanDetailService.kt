package com.example.savvy_android.plan.service

import com.example.savvy_android.plan.data.PlanDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PlanDetailService {
    @GET("planner/54")
    fun planDetail(
        @Header("Authorization") token: String,
    ): Call<PlanDetailResponse>
}