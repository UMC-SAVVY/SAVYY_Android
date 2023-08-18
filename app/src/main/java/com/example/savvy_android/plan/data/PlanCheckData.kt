package com.example.savvy_android.plan.data

data class PlanCheckRequest(
    val checklist: MutableList<DetailChecklist>
)

data class DetailChecklist(
    val id: Long?,
    var contents: String,
    var is_checked: Int
)

data class PlanCheckResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)
