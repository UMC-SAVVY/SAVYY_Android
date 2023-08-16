package com.example.savvy_android.myPage.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityMypageBlockBinding
import com.example.savvy_android.myPage.adapter.MypageBlockAdapter
import com.example.savvy_android.myPage.data.MyPageBlockResult

class MypageBlockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBlockBinding
    private lateinit var myPageBlockAdapter: MypageBlockAdapter
    private var myPageBlockData = arrayListOf<MyPageBlockResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityMypageBlockBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 뒤로 가기 버튼 클릭 이벤트
        binding.storagePlaceArrowIv.setOnClickListener {
            finish()
        }
        // SearchNewPlace Data & Adapter
        myPageBlockAdapter = MypageBlockAdapter(myPageBlockData)
        binding.storageRecycle.adapter = myPageBlockAdapter
    }
}