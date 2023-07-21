package com.example.savvy_android.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityLoginBinding
import com.example.savvy_android.myPage.activity.ProfileSettingActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))


        // 로그인 버튼 클릭시 이벤트
        binding.kakaoLoginBtn.setOnClickListener {

            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("LOGIN", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    //서버와 통신 파트



                    // 홈 화면으로 연결
                    val intent = Intent(this, MainActivity::class.java)
                    // 홈 화면으로 연결되면 이전에 존재하던 splash, login acitivty 종료
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("LOGIN", "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i("LOGIN", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        //서버와 통신 파트



                        // 홈 화면으로 연결
                        val intent = Intent(this, MainActivity::class.java)
                        // 홈 화면으로 연결되면 이전에 존재하던 splash, login acitivty 종료
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }

//            // 카카오 API 연결 필요
//            var hasRegistered = true
//
//            if(hasRegistered){
//                // 홈 화면으로 연결
//                val intent = Intent(this, MainActivity::class.java)
//                // 홈 화면으로 연결되면 이전에 존재하던 splash, login acitivty 종료
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            }else{
//                // 회원가입 화면으로 연결
//                val intent = Intent(this, ProfileSettingActivity::class.java)
//                intent.putExtra("isMyPage",false)
//                startActivity(intent)
//            }
        }
    }
}