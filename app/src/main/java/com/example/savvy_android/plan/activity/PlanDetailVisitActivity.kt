package com.example.savvy_android.plan.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityPlanDetailVisitBinding
import com.example.savvy_android.plan.adapter.DetailDateAdapter
import com.example.savvy_android.plan.dialog.PlanGetDialogFragment
import com.example.savvy_android.plan.dialog.PlanScrapDialogFragment
import com.example.savvy_android.utils.BottomSheetOtherDialogFragment
import com.example.savvy_android.utils.report.ReportActivity

class PlanDetailVisitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanDetailVisitBinding
    private lateinit var viewDateAdapter: DetailDateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityPlanDetailVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        viewDateAdapter = DetailDateAdapter(mutableListOf("", "", ""))
        binding.recyclerviewViewDate.adapter = viewDateAdapter
        binding.recyclerviewViewDate.layoutManager = LinearLayoutManager(this)

        // 계획서 가져오기 클릭
        binding.planGetBtn.setOnClickListener {
            val dialog = PlanGetDialogFragment()
            dialog.show(supportFragmentManager, "planGetDialog")

        }

        // 계획서 스크랩하기 클릭
        binding.planScrapBtn.setOnClickListener {
            val dialog = PlanScrapDialogFragment()
            dialog.show(supportFragmentManager, "planScrapDialog")
        }


        // 뒤로 가기 버튼 클릭
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // 옵션 관련 (다른사람이 작성한 다이어리)
        val bottomSheetOther = BottomSheetOtherDialogFragment()
        bottomSheetOther.setButtonClickListener(object :
            BottomSheetOtherDialogFragment.OnButtonClickListener {
            override fun onDialogReportClicked() {
                val intent = Intent(this@PlanDetailVisitActivity, ReportActivity::class.java)
                startActivity(intent)
            }
        })


        //option 클릭하면 bottom sheet
        binding.optionBtn.setOnClickListener {
            bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")

        }
    }
}