package com.example.savvy_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
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

        // add_date_btn 클릭 시 새로운 날짜 추가
        binding.addDateBtn.setOnClickListener {
            val newItem = ""
            dateAddAdapter.addItem(newItem)
        }


        // RecyclerView에 PlanMakeAdapter 설정
        dateAddAdapter = DateAddAdapter(mutableListOf(""))
        binding.recyclerviewDateAdd.adapter = dateAddAdapter
        binding.recyclerviewDateAdd.layoutManager = LinearLayoutManager(this)


        // 메모 추가하기 버튼 클릭 이벤트
        binding.memoAddBtn.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            startActivity(intent)
        }
    }

}