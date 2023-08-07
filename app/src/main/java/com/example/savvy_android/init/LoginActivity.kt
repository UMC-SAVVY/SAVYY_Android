package com.example.savvy_android.init

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityLoginBinding
import com.example.savvy_android.init.data.LoginRequest
import com.example.savvy_android.init.data.LoginResponse
import com.example.savvy_android.init.service.LoginService
import com.example.savvy_android.utils.LoadingDialogFragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)

        setupLoginButton()
    }

    private fun setupLoginButton() {
        // 로그인 버튼 클릭시 이벤트
        binding.kakaoLoginBtn.setOnClickListener {

            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "[LOGIN KAKAO] 카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.d("LOGIN", "[LOGIN KAKAO] 카카오계정으로 로그인 성공")
                    // 서버와 통신 파트

                    val dialog = LoadingDialogFragment()
                    dialog.show(supportFragmentManager, "LoadingDialog")

                    // 서버 주소
                    val serverAddress = getString(R.string.serverAddress)

                    val retrofit = Retrofit.Builder()
                        .baseUrl(serverAddress)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val loginService = retrofit.create(LoginService::class.java)
                    val accessToken = token.accessToken

                    // 로그인 요청을 위한 데이터 객체 생성
                    val loginRequest = LoginRequest(accessToken)

                    // 로그인 API 호출
                    loginService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>,
                        ) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                // 서버 응답 처리 로직 작성
                                if (loginResponse?.isSuccess == true) {
                                    Log.d(
                                        "LOGIN",
                                        "[LOGIN ACCOUNT] 성공"
                                    )


                                    // 서버 토큰과 닉네임을 SharedPreferences에 저장
                                    saveServerToken(loginResponse.result.token)
                                    saveNickname(loginResponse.result.nickname)

                                    // 홈 화면으로 연결
                                    moveToMainActivity()
                                } else {
                                    loginResponse?.let {
                                        errorCodeList(
                                            errorCode = it.code,
                                            message = it.message,
                                            type = "LOGIN",
                                            detailType = "ACCOUNT",
                                            intentData = token.accessToken
                                        )
                                    }
                                }
                            } else {
                                Log.e(
                                    "LOGIN",
                                    "[LOGIN ACCOUNT] API 호출 실패 - 응답 코드: ${response.code()}"
                                )
                            }
                            dialog.dismiss()
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                            Log.e("LOGIN", "[LOGIN ACCOUNT] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                            dialog.dismiss()
                        }
                    })
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("LOGIN", "[LOGIN KAKAO] 카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.d(
                            "LOGIN",
                            "[LOGIN KAKAO] 카카오톡으로 로그인 성공"
                        )
                        // 서버와 통신 파트

                        val dialog = LoadingDialogFragment()
                        dialog.show(supportFragmentManager, "LoadingDialog")

                        // 서버 주소
                        val serverAddress = getString(R.string.serverAddress)

                        val retrofit = Retrofit.Builder()
                            .baseUrl(serverAddress)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val loginService = retrofit.create(LoginService::class.java)

                        val accessToken = token.accessToken

                        // 로그인 요청을 위한 데이터 객체 생성
                        val loginRequest = LoginRequest(accessToken)

                        // 로그인 API 호출
                        loginService.login(loginRequest)
                            .enqueue(object : Callback<LoginResponse> {
                                override fun onResponse(
                                    call: Call<LoginResponse>,
                                    response: Response<LoginResponse>,
                                ) {
                                    if (response.isSuccessful) {
                                        val loginResponse = response.body()
                                        // 서버 응답 처리 로직 작성
                                        if (loginResponse?.isSuccess == true) {
                                            Log.d(
                                                "LOGIN",
                                                "[LOGIN APP] 로그인 성공"
                                            )

                                            // 서버 토큰과 닉네임을 SharedPreferences에 저장
                                            saveServerToken(loginResponse.result.token)
                                            saveNickname(loginResponse.result.nickname)

                                            // 홈 화면으로 연결
                                            moveToMainActivity()
                                        } else {
                                            loginResponse?.let {
                                                errorCodeList(
                                                    errorCode = it.code,
                                                    message = it.message,
                                                    type = "LOGIN",
                                                    detailType = "APP",
                                                    intentData = token.accessToken
                                                )
                                            }
                                        }
                                    } else {
                                        Log.e(
                                            "LOGIN",
                                            "[LOGIN APP] API 호출 실패 - 응답 코드: ${response.code()}"
                                        )
                                    }
                                    dialog.dismiss()
                                }

                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                                    Log.e(
                                        "LOGIN",
                                        "[LOGIN APP] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                                    )
                                    dialog.dismiss()
                                }
                            })
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    private fun saveServerToken(serverToken: String) {
        // 서버 토큰을 SharedPreferences에 저장
        val editor = sharedPreferences.edit()
        editor.putString("SERVER_TOKEN_KEY", serverToken)
        editor.apply()
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
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}