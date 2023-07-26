package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.databinding.ActivityDiaryStep3Binding
import com.example.savvy_android.diary.adapter.Make3Adapter
import com.example.savvy_android.diary.data.DiaryDetailItemData
import com.example.savvy_android.diary.dialog.DiarySaveDialogFragment

class DiaryMake3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep3Binding
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var make3Adapter: Make3Adapter
    private var diaryDetailData = arrayListOf<DiaryDetailItemData>()
    private var isDiary: Boolean = true

    private val SELECT_PLACE_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작된 fragment 정보 받기
        isDiary = intent.getBooleanExtra("isDiary",true)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //seek bar 애니메이션
        binding.seekBar.max = 4000

        valueAnimator = ValueAnimator.ofInt(2000, 3000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }

        valueAnimator.start()

        binding.diaryNextBtn.setOnClickListener {
            val intent = Intent(this, DiaryMake4Activity::class.java)
            intent.putExtra("isDiary",isDiary)
            startActivity(intent)
        }

        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }


        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep3Tv.text.toString())

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 34, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan2, 48, 57, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan3 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan3, 64, 75, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan4 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan4, 85, 97, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep3Tv.text = spannableString

        //다이어리 내용 리사이클뷰 리스트
        make3Adapter = Make3Adapter(diaryDetailData)
        binding.diaryDetailRecycle.adapter = make3Adapter
        binding.diaryDetailRecycle.layoutManager = LinearLayoutManager(this)

        binding.diaryContentBtn.setOnClickListener {
            make3Adapter.addDiary(
                DiaryDetailItemData(
                    position = make3Adapter.itemCount,
                    isText = true,
                    text = null,
                    image = null,
                    hasPlace = false,
                    placeName = null,
                    placeUrl = null
                )
            )
        }

        binding.diaryImageBtn.setOnClickListener {
            // 갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            addImage.launch(intent)

        }

        binding.diaryPlanBtn.setOnClickListener {
            val intent = Intent(this, PlanDetailActivity::class.java)
            startActivity(intent)
        }
    }

    // 갤러리에서 선택한 이미지 결과 가져오기
    private val addImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 결과 코드 OK, 결과값 not null 일 때
        if (it.resultCode == RESULT_OK && it.data != null) {
            val uri = it.data!!.data    // 결과 값 저장

            make3Adapter.addDiary(
                DiaryDetailItemData(
                    position = make3Adapter.itemCount,
                    isText = false,
                    text = null,
                    image = uri.toString(),
                    hasPlace = false,
                    placeName = null,
                    placeUrl = null
                )
            )
        }
    }

    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiarySaveDialogFragment(isDiary)
        dialog.show(supportFragmentManager, "diarySaveDialog")
    }

}