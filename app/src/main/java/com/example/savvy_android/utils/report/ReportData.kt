package com.example.savvy_android.utils.report

data class ReportRequest(
    val planner_id : Int,
    val reason_1 : Int,
    val reason_2 : Int,
    val reason_3 : Int,
    val reason_4 : Int,
    val contents : String,
    val is_blocked : Int
)

data class ReportResponse(
    val isSuccess : Boolean,
    val code : Int,
    val message : String
)