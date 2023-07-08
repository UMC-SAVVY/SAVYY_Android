package com.example.savvy_android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.databinding.ActivityMemoModifyBinding

class MemoModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoModifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityMemoModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //메모 입력 변화 이벤트 처리
        binding.memoEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트 변경 전 동작
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중 동작
            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0

                //한글자라도 입력하면 '등록'버튼의 색이 바뀜
                if (textLength > 0) {
                    binding.memoEdit.setTextColor(Color.BLACK)
                    binding.memoRegistrationBtn.setTextColor(Color.parseColor("#FF5487"))
                } else {
                    binding.memoEdit.setTextColor(Color.parseColor("#5F5F5F"))
                    binding.memoRegistrationBtn.setTextColor(Color.BLACK)
                }
            }
        })


        // arrowLeft 아이콘 클릭하면 저장하지 않고 여행 계획서 보기 페이지로 돌아가기
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // memoRegistration 클릭하면 한 글자 이상 입력되었을 때만 TravelPlanMakeActivity로 돌아가기
        binding.memoRegistrationBtn.setOnClickListener {
            val memoText = binding.memoEdit.text.toString().trim()
            if (memoText.isNotEmpty()) {
                val intent = Intent(this, TravelPlanModifyActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
