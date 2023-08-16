package com.example.savvy_android.diary.service

import com.example.savvy_android.diary.data.detail.DiaryDetailResponse
import com.example.savvy_android.diary.data.list.DiaryListResponse
import com.example.savvy_android.diary.data.make_modify.DiaryMakeRequest
import com.example.savvy_android.diary.data.make_modify.DiaryMakeModifyResponse
import com.example.savvy_android.diary.data.make_modify.DiaryModifyRequest
import com.example.savvy_android.init.data.image.MultipleImageResponse
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryService {
    @GET("")
    fun diaryListAll(
        @Header("Authorization") token: String,
    ): Call<DiaryListResponse>

    @GET("diary/list/mydiary")
    fun diaryListMine(
        @Header("Authorization") token: String,
    ): Call<DiaryListResponse>

    @GET("planner/search")
    fun diaryListSearch(
        @Header("Authorization") token: String,
        @Query("searchWord") word: String,
    ): Call<DiaryListResponse>

    @DELETE("diary/{diary_id}")
    fun diaryDelete(
        @Header("Authorization") token: String,
        @Path("diary_id") diaryID: String,
    ): Call<ServerDefaultResponse>

    @GET("diary/{diary_id}")
    fun diaryDetail(
        @Header("Authorization") token: String,
        @Path("diary_id") diaryId: String,
    ): Call<DiaryDetailResponse>

    @POST("diary")
    fun diaryMake(
        @Header("Authorization") token: String,
        @Body diaryMakeRequest: DiaryMakeRequest,
    ): Call<DiaryMakeModifyResponse>

    @PUT("diary")
    fun diaryModify(
        @Header("Authorization") token: String,
        @Body diaryModifyRequest: DiaryModifyRequest,
    ): Call<DiaryMakeModifyResponse>

    @Multipart
    @POST("diary/image")
    fun diaryImage(
        @Header("Authorization") token: String,
        @Part imageFileList: ArrayList<MultipartBody.Part>,
    ): Call<MultipleImageResponse>

    @POST("diary/status")
    fun diaryStatus(
        @Header("Authorization") token: String,
        @Query("type") type: String,
        @Query("value") value: String,
        @Query("diary_id") diaryID: Int,
    ): Call<ServerDefaultResponse>
}