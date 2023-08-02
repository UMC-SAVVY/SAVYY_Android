package com.example.savvy_android.plan.service

import com.example.savvy_android.plan.data.PlanDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PlanDetailService {
    @GET("planner/{planner_id}")
    fun planDetail(
        @Header("Authorization") token: String,
        @Path("planner_id") plannerId: String
    ): Call<PlanDetailResponse>
}