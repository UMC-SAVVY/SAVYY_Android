package com.example.savvy_android.init.service

import com.example.savvy_android.init.data.LoginRequest
import com.example.savvy_android.init.data.LoginResponse
import com.example.savvy_android.init.data.autoLogin.autoLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {
    @POST("user/login")
    fun login(
        @Body loginRequest: LoginRequest,
    ): Call<LoginResponse>

    @GET("user/login")
    fun autoLogin(
        @Header("Authorization") token: String,
    ): Call<autoLoginResponse>
}
