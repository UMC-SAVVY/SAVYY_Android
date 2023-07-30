package com.example.savvy_android.plan.service

import com.example.savvy_android.plan.data.PlanMakeRequest
import com.example.savvy_android.plan.data.PlanMakeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PlanMakeService{
    @POST("planner")
    fun planMake(
        @Header("Authorization") token: String,
        @Body planMakeRequest: PlanMakeRequest
    ): Call<PlanMakeResponse>
}
