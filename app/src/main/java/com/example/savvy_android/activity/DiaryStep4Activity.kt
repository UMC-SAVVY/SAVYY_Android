package com.example.savvy_android.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.adapter.DiaryHashtagAdapter
import com.example.savvy_android.databinding.ActivityDiaryStep4Binding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.dialog.DiarySaveDialogFragment

class DiaryStep4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep4Binding
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var diaryHashtagAdapter: DiaryHashtagAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //seek bar 애니메이션
        binding.seekBar.max = 4000

        valueAnimator = ValueAnimator.ofInt(3000, 4000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }

        valueAnimator.start()


        // RecyclerView에 HashtagAdapter 설정
        diaryHashtagAdapter = DiaryHashtagAdapter(mutableListOf("", "", ""))
        binding.recyclerviewHashtag.adapter = diaryHashtagAdapter
        binding.recyclerviewHashtag.layoutManager = LinearLayoutManager(this)


        binding.diaryNextBtn.setOnClickListener {

            // 커스텀 Toast 메시지 생성
            val toastBinding = LayoutToastBinding.inflate(layoutInflater)
            toastBinding.toastMessage.text = "성공적으로 다이어리 작성이 완료되었습니다"

            val toast = Toast(this)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastBinding.root

            toast.setGravity(Gravity.TOP, 0, 120)  //toast 위치 설정

            toast.show()

            //임시 연결
            //다이어리 fragment로 이동해야 함
            val intent = Intent(this, TravelPlanViewActivity::class.java)
            startActivity(intent)
        }



        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }


        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep4Tv.text.toString())

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 75, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep4Tv.text = spannableString

    }

    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiarySaveDialogFragment()
        dialog.show(supportFragmentManager, "diarySaveDialog")
    }

}