package com.example.savvy_android.init

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivitySplashBinding
import com.example.savvy_android.init.data.autoLogin.AutoLoginResponse
import com.example.savvy_android.init.service.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)
        val serverToken =
            sharedPreferences.getString("SERVER_TOKEN_KEY", null)  // 서버토큰

        if (serverToken != null) {
            // 서버 토큰이 이미 존재하면 자동 로그인을 수행하고 MainActivity
            autoLoginAPI(serverToken)
        } else {
            animationDuring(false)
        }
    }

    private fun autoLoginAPI(serverToken: String) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginService = retrofit.create(LoginService::class.java)

        // 로그인 API 호출
        loginService.autoLogin(serverToken).enqueue(object : Callback<AutoLoginResponse> {
            override fun onResponse(
                call: Call<AutoLoginResponse>,
                response: Response<AutoLoginResponse>,
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    // 서버 응답 처리 로직 작성
                    if (loginResponse?.isSuccess == true) {
                        Log.d("LOGIN", "[LOGIN AUTO] 성공 - 서버에서 받은 토큰: $serverToken")
                        // 홈 화면으로 연결
                        saveNickname(loginResponse.result.nickname)
                        animationDuring(true)
                    } else {
                        loginResponse?.let {
                            errorCodeList(
                                errorCode = it.code,
                                message = it.message,
                                type = "LOGIN",
                                detailType = "AUTO",
                                intentData = serverToken
                            )
                        }
                        animationDuring(false)
                    }
                } else {
                    Log.e("LOGIN", "[LOGIN AUTO] API 호출 실패 - 응답 코드: ${response.code()}")
                    animationDuring(false)
                }
            }

            override fun onFailure(call: Call<AutoLoginResponse>, t: Throwable) {
                // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                Log.e("LOGIN", "[LOGIN AUTO] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                animationDuring(false)
            }
        })
    }

    private fun saveNickname(nickname: String) {
        // 서버 토큰을 SharedPreferences에 저장
        val editor = sharedPreferences.edit()
        editor.putString("USER_NICKNAME", nickname)
        editor.apply()
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // 홈 화면으로 연결되면 이전에 존재하던 splash, login activity 종료
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun animationDuring(isSuccess: Boolean) {
        // splash 화면을 정해진 시간동안 보여주고 다음 화면으로 넘어가는 code
        Handler().postDelayed({
            if (isSuccess)
            // 서버 토큰이 이미 존재하면 자동 로그인을 수행하고 MainActivity
                moveToMainActivity()
            else
                moveToLoginActivity()
        }, DURATION)
    }

    companion object {
        // 2초 동안 splash 화면 유지
        private const val DURATION: Long = 2000
    }
}