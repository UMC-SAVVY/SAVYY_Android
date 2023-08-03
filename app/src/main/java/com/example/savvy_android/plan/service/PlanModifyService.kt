package com.example.savvy_android.plan.service

import retrofit2.Call
import com.example.savvy_android.plan.data.PlanModifyRequest
import com.example.savvy_android.plan.data.PlanModifyResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface PlanModifyService{
    @PUT("planner")
    fun planModify(
        @Header("Authorization") token: String,
        @Body planModifyRequest: PlanModifyRequest
    ): Call<PlanModifyResponse>
}
