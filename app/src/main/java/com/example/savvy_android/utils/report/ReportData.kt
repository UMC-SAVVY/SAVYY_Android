package com.example.savvy_android.utils.report

// 여행계획서 신고
data class PlanReportRequest(
    val planner_id : Int,
    val reason_1 : Int,
    val reason_2 : Int,
    val reason_3 : Int,
    val reason_4 : Int,
    val contents : String,
    val is_blocked : Int
)

// 다이어리 신고
data class DiaryReportRequest(
    val diary_id : Int,
    val reason_1 : Int,
    val reason_2 : Int,
    val reason_3 : Int,
    val reason_4 : Int,
    val contents : String,
    val is_blocked : Int
)


// 댓글 신고
data class CommentReportRequest(
    val comment_id : Int,
    val reason_1 : Int,
    val reason_2 : Int,
    val reason_3 : Int,
    val reason_4 : Int,
    val contents : String,
    val is_blocked : Int
)

// 답글 신고
data class NestedCommentReportRequest(
    val reply_id : Int,
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
