package com.example.savvy_android.utils.alarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private var alarmData = arrayListOf<AlarmItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 뒤로 가기 버튼 클릭 이벤트
        binding.alarmArrowIv.setOnClickListener {
            finish()

            // alarm Data & Adapter
            alarmAdapter = AlarmAdapter(alarmData)
            binding.storageRecycle.adapter = alarmAdapter

        }
    }
}