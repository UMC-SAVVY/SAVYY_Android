package com.example.savvy_android.utils.memo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityMemoBinding
import com.example.savvy_android.plan.activity.PlanMakeActivity

class MemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 메모 추가하기 인지, 메모 확인하기 인지 구분
        val isMemoAdd = intent.getBooleanExtra("isMemoAdd", true)
        if (isMemoAdd) { // 여행계획서 작성에서 연결 경우 (메모 추가하기)
            binding.memoRegistrationBtn.visibility = View.VISIBLE
        } else { // 여행계획서 보기에서 연결 경우 (메모 확인하기)
            binding.memoRegistrationBtn.visibility = View.GONE
        }

        val memoText = intent.getStringExtra("memoText")

        // 메모 데이터를 띄워주기
        binding.memoEdit.setText(memoText)

        //메모 입력 변화 이벤트 처리
        binding.memoEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트 변경 전 동작
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중 동작
                val textLength = s?.length ?: 0

                //한글자라도 입력하면 '등록'버튼의 색이 바뀜
                if (textLength > 0) {
                    binding.memoRegistrationBtn.setTextColor(Color.parseColor("#FF5487"))
                } else {
                    binding.memoRegistrationBtn.setTextColor(Color.parseColor("#5F5F5F"))
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // 등록 버튼의 색 설정
        if (memoText != null && memoText.isNotEmpty()) {
            binding.memoRegistrationBtn.setTextColor(Color.parseColor("#FF5487"))
        } else {
            binding.memoRegistrationBtn.setTextColor(Color.parseColor("#5F5F5F"))
        }


        // arrowLeft 아이콘 클릭하면 저장하지 않고 여행 계획서 페이지로 돌아가기
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // memoRegistration 클릭하면 한 글자 이상 입력되었을 때만 TravelPlanMakeActivity로 돌아가기
        binding.memoRegistrationBtn.setOnClickListener {
            val memoText = binding.memoEdit.text.toString().trim()
            if (memoText.isNotEmpty()) {

                val resultIntent = Intent()
                resultIntent.putExtra("memoText", memoText)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
