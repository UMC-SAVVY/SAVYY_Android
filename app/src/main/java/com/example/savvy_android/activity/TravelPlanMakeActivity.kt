package com.example.savvy_android.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.adapter.DateAddAdapter
import com.example.savvy_android.databinding.ActivityTravelPlanMakeBinding

class TravelPlanMakeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTravelPlanMakeBinding
    private lateinit var dateAddAdapter: DateAddAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityTravelPlanMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //임시 title 입력 변화 이벤트 처리
        binding.titleEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트 변경 전 동작
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중 동작
            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0

                //한글자라도 입력하면 '완료'버튼의 색이 바뀜
                if (textLength > 0) {
                    binding.makeCompletionBtn.setTextColor(Color.parseColor("#FF5487"))
                } else {
                    binding.titleEdit.setTextColor(Color.parseColor("#5F5F5F"))
                }
            }
        })

        // add_date_btn 클릭 시 새로운 날짜 추가
        binding.addDateBtn.setOnClickListener {
            val newItem = ""
            dateAddAdapter.addItem(newItem)
        }

        // RecyclerView에 DateAddAdapter 설정
        dateAddAdapter = DateAddAdapter(mutableListOf(""), supportFragmentManager)
        binding.recyclerviewDateAdd.adapter = dateAddAdapter
        binding.recyclerviewDateAdd.layoutManager = LinearLayoutManager(this)

        // 뒤로가기 클릭 이벤트
        binding.icArrowLeft.setOnClickListener {
            finish()
        }

        // 만들기 완료 버튼 클릭 이벤트
        binding.makeCompletionBtn.setOnClickListener {
            val titleText = binding.titleEdit.text.toString()
            if (titleText.isNotEmpty()) {
                finish()
            }
        }

        // 메모 추가하기 버튼 클릭 이벤트
        binding.memoAddBtn.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            startActivity(intent)
        }

        // 뒤로가기 버튼 클릭 이벤트
        binding.icArrowLeft.setOnClickListener {
            finish()
        }
    }

}