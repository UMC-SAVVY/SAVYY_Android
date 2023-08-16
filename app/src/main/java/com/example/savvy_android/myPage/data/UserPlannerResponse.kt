package com.example.savvy_android.myPage.data

import com.example.savvy_android.plan.data.list.PlanListResult

data class UserPlannerResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: Planner,
)

data class Planner(
    val planner: List<PlanListResult>,
    val amount_planner: Int,
)