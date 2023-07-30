package com.example.savvy_android.plan.service

import com.example.savvy_android.plan.data.remove.PlanRemoveResponse
import com.example.savvy_android.plan.data.list.PlanListResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PlanListService {
    @GET("planner/list")
    fun planListAll(
        @Header("Authorization") token: String,
    ): Call<PlanListResponse>

    @GET("planner/list/myplanner")
    fun planListMine(
        @Header("Authorization") token: String,
    ): Call<PlanListResponse>

    @GET("planner/list/scrap")
    fun planListScrap(
        @Header("Authorization") token: String,
    ): Call<PlanListResponse>

    @GET("planner/search")
    fun planListSearch(
        @Header("Authorization") token: String,
        @Query("searchWord") word: String,
    ): Call<PlanListResponse>

    @DELETE("planner")
    fun planDelete(
        @Header("Authorization") token: String,
        @Query("plannerId") plannerId: String,
        @Query("type") plannerType: String,
    ): Call<PlanRemoveResponse>
}