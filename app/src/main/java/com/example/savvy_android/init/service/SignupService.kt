package com.example.savvy_android.init.service

import com.example.savvy_android.init.data.SignupRequest
import com.example.savvy_android.init.data.SignupResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupService{
    @POST("user/signup")
    fun signup(
        @Body signupRequest: SignupRequest
    ): Call<SignupResponse>
}