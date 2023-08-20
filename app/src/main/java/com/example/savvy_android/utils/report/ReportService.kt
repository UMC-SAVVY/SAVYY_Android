package com.example.savvy_android.utils.report

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// 여행계획서 신고
interface PlanReportService {
    @POST("planner/report")
    fun planReport(
        @Header("Authorization") token: String,
        @Body planReportRequest: PlanReportRequest
    ): Call<ReportResponse>
}

// 다이어리 신고
interface DiaryReportService {
    @POST("diary/report")
    fun diaryReport(
        @Header("Authorization") token: String,
        @Body diaryReportRequest: DiaryReportRequest
    ): Call<ReportResponse>
}

// 댓글 신고
interface CommentReportService {
    @POST("comment/report")
    fun commentReport(
        @Header("Authorization") token: String,
        @Query("type") type: String,
        @Body commentReportRequest: CommentReportRequest
    ): Call<ReportResponse>
}

// 답글 신고
interface NestedCommentReportService {
    @POST("comment/report")
    fun nestedCommentReport(
        @Header("Authorization") token: String,
        @Query("type") type: String,
        @Body nestedCommentReportRequest: NestedCommentReportRequest
    ): Call<ReportResponse>
}

