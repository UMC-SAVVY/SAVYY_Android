package com.example.savvy_android.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityLoginBinding
import com.example.savvy_android.myPage.activity.ProfileSettingActivity

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

            // 카카오 API 연결 필요
            var hasRegistered = true

            if(hasRegistered){
                // 홈 화면으로 연결
                val intent = Intent(this, MainActivity::class.java)
                //val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                // 회원가입 화면으로 연결
                val intent = Intent(this, ProfileSettingActivity::class.java)
                startActivity(intent)
            }
        }
    }
}