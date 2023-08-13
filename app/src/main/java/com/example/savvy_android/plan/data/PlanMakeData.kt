package com.example.savvy_android.plan.data

data class PlanMakeRequest(
    val id: Long?,
    val memo: String,
    val nickname: String,
    val timetable: MutableList<Timetable>,
    val title: String,
    val updated_at: String
)

data class Timetable(
    var date: String,
    val schedule: MutableList<Schedule>
)

data class Schedule(
    val id: Long?,
    val checklist: MutableList<Checklist>,
    val finished_at: String,
    var place_name: String,
    val started_at: String
)

data class Checklist(
    val id: Long?,
    var contents: String,
    var is_checked: Int
)

data class PlanMakeResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)