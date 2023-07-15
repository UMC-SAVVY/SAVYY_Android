package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.diary.adapter.Make1Adapter
import com.example.savvy_android.databinding.ActivityDiaryStep1Binding
import com.example.savvy_android.diary.dialog.DiaryStopDialogFragment
import com.example.savvy_android.diary.dialog.NextStepDialogFragment
import com.example.savvy_android.diary.dialog.PlanSelectDialogFragment

class DiaryMake1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep1Binding
    private lateinit var diaryPlanListAdapter: Make1Adapter
    private lateinit var valueAnimator: ValueAnimator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        diaryPlanListAdapter = Make1Adapter(mutableListOf("", "", "", "", ""))
        binding.recyclerviewDiaryList.adapter = diaryPlanListAdapter
        binding.recyclerviewDiaryList.layoutManager = LinearLayoutManager(this)


        //seek bar 애니메이션
        binding.seekBar.max = 4000

        valueAnimator = ValueAnimator.ofInt(0, 1000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }

        valueAnimator.start()

        binding.diaryNextBtn.setOnClickListener {
            if (diaryPlanListAdapter.clickedPositions.isNotEmpty()) {
                val planSelectDialog = PlanSelectDialogFragment()
                planSelectDialog.show(supportFragmentManager, "planSelectDialog")
            } else {
                val nextStepDialog = NextStepDialogFragment()
                nextStepDialog.show(supportFragmentManager, "nextStepDialog")
            }
        }

        // 뒤로가기 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }


        //다이어리 찾기 edit 버튼 활성화
        binding.diarySearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.diarySearchEdit.length() != 0
                binding.diarySearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.diarySearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep1Tv.text.toString())

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 29, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan2, 60, 62, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep1Tv.text = spannableString

    }

    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val buttonColor = if (able) {
            ContextCompat.getColor(button.context, R.color.main)
        } else {
            ContextCompat.getColor(button.context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }


    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiaryStopDialogFragment()
        dialog.show(supportFragmentManager, "diaryStopDialog")
    }

}