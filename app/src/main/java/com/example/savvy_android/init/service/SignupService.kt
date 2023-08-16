package com.example.savvy_android.init.service

import com.example.savvy_android.init.data.SignupRequest
import com.example.savvy_android.init.data.SignupResponse
import com.example.savvy_android.init.data.image.SingleImageResponse
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface SignupService {
    @POST("user/signup")
    fun signup(
        @Body signupRequest: SignupRequest,
    ): Call<SignupResponse>

    @Multipart
    @POST("user/image/profile")
    fun uploadProfile(
        @Part imageFile: MultipartBody.Part,
    ): Call<SingleImageResponse>

    @GET("user/duplication")
    fun duplicate(
        @Query("nickname") nickname: String,
    ): Call<ServerDefaultResponse>
}