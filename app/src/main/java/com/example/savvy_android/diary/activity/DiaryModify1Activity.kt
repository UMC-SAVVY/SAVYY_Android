package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityDiaryModify1Binding
import com.example.savvy_android.plan.activity.PlanDetailActivity

class DiaryModify1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryModify1Binding
    private lateinit var valueAnimator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryModify1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //seek bar 애니메이션
        binding.seekBar.max = 4000

        valueAnimator = ValueAnimator.ofInt(0, 2000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }

        valueAnimator.start()

        binding.diaryNextBtn.setOnClickListener {
            val intent = Intent(this, DiaryModify2Activity::class.java)
            startActivity(intent)
        }

        binding.diaryContentBtn.setOnClickListener {

        }

        binding.diaryPictureBtn.setOnClickListener {
        }


        binding.diaryPlanBtn.setOnClickListener {
            val intent = Intent(this, PlanDetailActivity::class.java)
            startActivity(intent)
        }


        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }
    }
}
