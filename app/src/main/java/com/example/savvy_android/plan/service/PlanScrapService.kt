package com.example.savvy_android.plan.service

import com.example.savvy_android.plan.data.PlanMakeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PlanScrapService {
    @POST("planner/scrap")
    fun planScrap(
        @Header("Authorization") token: String,
        @Body planID: PlanIDRequest,
    ): Call<PlanMakeResponse>
}

data class PlanIDRequest(
    val planner_id: Int
)
