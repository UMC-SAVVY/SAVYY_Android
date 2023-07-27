package com.example.savvy_android.plan.data.list

data class PlanListResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<PlanListResult>
)