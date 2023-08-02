package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.plan.adapter.MakeDateAddAdapter
import com.example.savvy_android.databinding.ActivityDiaryStep2Binding
import com.example.savvy_android.diary.dialog.DiaryStopDialogFragment
import com.example.savvy_android.plan.data.Checklist
import com.example.savvy_android.plan.data.Schedule
import com.example.savvy_android.plan.data.Timetable

class DiaryMake2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep2Binding
    private lateinit var dateAddAdapter : MakeDateAddAdapter
    private lateinit var valueAnimator: ValueAnimator
    private var isDiary: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작된 fragment 정보 받기
        isDiary = intent.getBooleanExtra("isDiary",true)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // RecyclerView에 DateAddAdapter 설정
        dateAddAdapter = MakeDateAddAdapter(mutableListOf(), supportFragmentManager, false)
        binding.recyclerviewTravelPlan.adapter = dateAddAdapter
        binding.recyclerviewTravelPlan.layoutManager = LinearLayoutManager(this)

        val showDateAddItem = intent.getBooleanExtra("showDateAddItem", false)
        if (showDateAddItem) {
            // DateAdd 아이템 추가
//            dateAddAdapter.addItem("")

            val newTimetable = Timetable("", mutableListOf(
                Schedule(null, mutableListOf(
                    Checklist(null, "", 0)), "", "", "")))
            dateAddAdapter.addItem(newTimetable)

        }


        //seek bar 애니메이션
        binding.seekBar.max = 4000

        valueAnimator = ValueAnimator.ofInt(1000, 2000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }

        valueAnimator.start()

        binding.diaryNextBtn.setOnClickListener {
            val intent = Intent(this, DiaryMake3Activity::class.java)
            intent.putExtra("isDiary",isDiary)
            startActivity(intent)
        }

        // 뒤로가기 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // add_date_btn 클릭 시 새로운 날짜 추가
        binding.addDateBtn.setOnClickListener {
//            val newItem = ""
//            dateAddAdapter.addItem(newItem)

            val newTimetable = Timetable("", mutableListOf(Schedule(null, mutableListOf(Checklist(null, "", 0)), "", "", "")))
            dateAddAdapter.addItem(newTimetable)

        }



        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep2Tv.text.toString())

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 37, 49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan2, 70, 72, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep2Tv.text = spannableString
    }

    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiaryStopDialogFragment(isDiary)
        dialog.show(supportFragmentManager, "diaryStopDialog")
    }

}