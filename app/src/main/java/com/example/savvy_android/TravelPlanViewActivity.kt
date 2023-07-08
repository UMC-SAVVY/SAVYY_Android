package com.example.savvy_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.databinding.ActivityTravelPlanViewBinding
import com.example.savvy_android.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class TravelPlanViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTravelPlanViewBinding
    private lateinit var viewDateAdapter: ViewDateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityTravelPlanViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        val bottomSheetBinding = LayoutBottomSheetBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        viewDateAdapter = ViewDateAdapter(mutableListOf(""))
        binding.recyclerviewViewDate.adapter = viewDateAdapter
        binding.recyclerviewViewDate.layoutManager = LinearLayoutManager(this)



        //option 클릭하면 bottom sheet
        binding.optionBtn.setOnClickListener {
            bottomSheetDialog.show()
        }


        // 메모 확인하기 버튼 클릭 이벤트
        binding.memoCheckBtn.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            startActivity(intent)
        }

        // bottom sheet에서 수정하기 버튼 누르면 여행 계획서 수정 페이지로 이동
        bottomSheetBinding.editBtn.setOnClickListener {
            val intent = Intent(this, TravelPlanModifyActivity::class.java)
            intent.putExtra("planName", binding.travelPlanViewTitleTv.text.toString())
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }


    }
}