package com.example.savvy_android.init.service

import com.example.savvy_android.init.data.LoginRequest
import com.example.savvy_android.init.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService{
    @POST("user/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>
}
