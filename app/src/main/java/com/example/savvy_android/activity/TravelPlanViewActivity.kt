package com.example.savvy_android.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.adapter.ViewDateAdapter
import com.example.savvy_android.adapter.ViewDateOnlyAdapter
import com.example.savvy_android.databinding.ActivityTravelPlanViewBinding
import com.example.savvy_android.dialog.BottomSheetDialogFragment

class TravelPlanViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTravelPlanViewBinding
    private lateinit var viewDateAdapter: ViewDateAdapter
    private lateinit var viewDateOnlyAdapter: ViewDateOnlyAdapter
    private var isViewDate = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityTravelPlanViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        viewDateAdapter = ViewDateAdapter(mutableListOf(""))
        binding.recyclerviewViewDate.adapter = viewDateAdapter
        binding.recyclerviewViewDate.layoutManager = LinearLayoutManager(this)

        viewDateOnlyAdapter = ViewDateOnlyAdapter(mutableListOf(""))
        binding.recyclerviewViewDateOnly.adapter = viewDateOnlyAdapter
        binding.recyclerviewViewDateOnly.layoutManager = LinearLayoutManager(this)

        // 메모 수정 클릭
        binding.memoCheckBtn.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            startActivity(intent)
        }

        // 뒤로 가기 버튼 클릭
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // BottomSheet 관련
        val bottomSheet = BottomSheetDialogFragment()

        // BottomSheet 버튼 클릭 이벤트 설정
        bottomSheet.setButtonClickListener(object :
            BottomSheetDialogFragment.OnButtonClickListener {
            override fun onDialogEditClicked() {
                val intent = Intent(this@TravelPlanViewActivity, TravelPlanModifyActivity::class.java)
                intent.putExtra("planName", binding.travelPlanViewTitleTv.text.toString())
                startActivity(intent)
            }

            override fun onDialogDeleteClicked() {
            }
        })


        //option 클릭하면 bottom sheet
        binding.optionBtn.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")

        }
    }

    // arrowUpBtn 클릭 이벤트 처리
    fun onArrowUpBtnClicked() {
        if (isViewDate) {
            binding.recyclerviewViewDate.adapter = null
            binding.recyclerviewViewDate.visibility = View.GONE
            binding.recyclerviewViewDateOnly.adapter = viewDateOnlyAdapter
            binding.recyclerviewViewDateOnly.visibility = View.VISIBLE
            isViewDate = false
        }
    }

    // arrowDownBtn 클릭 이벤트 처리
    fun onArrowDownBtnClicked() {
        if (!isViewDate) {
            binding.recyclerviewViewDateOnly.adapter = null
            binding.recyclerviewViewDateOnly.visibility = View.GONE
            binding.recyclerviewViewDate.adapter = viewDateAdapter
            binding.recyclerviewViewDate.visibility = View.VISIBLE
            isViewDate = true
        }
    }
}