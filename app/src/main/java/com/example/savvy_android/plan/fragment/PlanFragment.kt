package com.example.savvy_android.plan.fragment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.savvy_android.R
import com.example.savvy_android.plan.activity.PlanMakeActivity
import com.example.savvy_android.plan.adapter.PlanListAdapter
import com.example.savvy_android.databinding.FragmentPlanBinding
import com.example.savvy_android.plan.PlanItemData
import com.example.savvy_android.plan.PlanItemTouchCallback


class PlanFragment : Fragment() {
    private lateinit var binding: FragmentPlanBinding
    private lateinit var planListAdapter: PlanListAdapter
    private var planListData = arrayListOf<PlanItemData>()
    private val planTouchSimpleCallback = PlanItemTouchCallback()
    private val itemTouchHelper = ItemTouchHelper(planTouchSimpleCallback)
    private var userName = "나"

    // 임시 데이터
    private var tmpDateList = arrayListOf(
        "2023.06.01",
        "2023.06.02",
        "2023.06.03",
        "2023.06.04",
        "2023.06.05",
        "2023.06.06",
        "2023.06.07",
        "2023.06.08",
        "2023.06.09",
        "2023.06.10",
    )
    private var tmpNameList = arrayListOf(
        "나", "셰이나", "루나", "나", "루나", "루나", "셰이나", "나", "셰이나", "루나"
    )
    // 여기 사이 dummy 데이터 삭제해야함.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlanBinding.inflate(inflater, container, false)

        // 알람 버튼 클릭시 알람 페이지 연결
        binding.planAlarm.setOnClickListener {
//            val intent = Intent(context, 알람 페이지 kotlin 파일)
//            startActivity(intent)
        }

        // 검색 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.planSearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.planSearchEdit.length() != 0
                binding.planSearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.planSearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Plan Data & Adapter
        planListAdapter =
            PlanListAdapter(
                binding.planRecycle,
                planListData,
                userName,
                requireActivity().supportFragmentManager,
                true
            )
        binding.planRecycle.adapter = planListAdapter


        // itemTouchHelper와 recyclerview 연결
        itemTouchHelper.attachToRecyclerView(binding.planRecycle)

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.planAddFbtn.setOnClickListener {
            val intent = Intent(context, PlanMakeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // 알람 존재 여부에 따른 알람 버튼 형태
        val hasAlarm = true
        if (hasAlarm)
            binding.planAlarm.setBackgroundResource(R.drawable.ic_alarm_o)
        else binding.planAlarm.setBackgroundResource(
            R.drawable.ic_alarm_x
        )

        // 임시 데이터 목록
        for (i: Int in 0 until tmpDateList.size) {
            planListAdapter.addPlan(
                PlanItemData(
                    i,
                    "하와이 여행 ${i + 1}",
                    tmpDateList[i],
                    tmpNameList[i],
                )
            )
        }

        // 검색 기능
        binding.planSearchBtn.setOnClickListener {
            Log.e("TEST", "검색 버튼 눌림")
            searchPlanList(binding.planSearchEdit.text.toString())
        }

        // 전체보기 클릭 이벤트
        binding.planFilterBtn1.setOnClickListener {
            btnClickColors(true, binding.planFilterBtn1)
            btnClickColors(false, binding.planFilterBtn2)
            btnClickColors(false, binding.planFilterBtn3)
            filterPlanList(1)
        }

        // 나의 계획서 클릭 이벤트
        binding.planFilterBtn2.setOnClickListener {
            btnClickColors(false, binding.planFilterBtn1)
            btnClickColors(true, binding.planFilterBtn2)
            btnClickColors(false, binding.planFilterBtn3)
            filterPlanList(2)
        }

        // 스크랩 클릭 이벤트
        binding.planFilterBtn3.setOnClickListener {
            btnClickColors(false, binding.planFilterBtn1)
            btnClickColors(false, binding.planFilterBtn2)
            btnClickColors(true, binding.planFilterBtn3)
            filterPlanList(3)
        }

    }

    // 클릭 가능 여부에 따른 button 배경 변경 함수
    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val context: Context = requireContext()
        val buttonColor = if (able) {
            ContextCompat.getColor(context, R.color.main)
        } else {
            ContextCompat.getColor(context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }

    // 필터 버튼들 클릭시 버튼 변경
    private fun btnClickColors(isClick: Boolean, button: AppCompatButton) {
        val context: Context = requireContext()
        if (isClick) {
            // 버튼 배경
            button.setBackgroundResource(R.drawable.btn_radius4)
            button.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main))
            button.setTextColor(ContextCompat.getColor(context, R.color.white)) // 버튼 텍스트 color
            button.typeface =
                ResourcesCompat.getFont(context, R.font.pretendard_bold)  // 버튼 텍스트 font
        } else {
            // 버튼 배경
            button.setBackgroundResource(R.drawable.btn_radius4)
            button.backgroundTintList = null
            button.setTextColor(ContextCompat.getColor(context, R.color.black)) // 버튼 텍스트 color
            button.typeface =
                ResourcesCompat.getFont(context, R.font.pretendard_regular)   // 버튼 텍스트 font
        }
    }

    // 목록 검색 API
    private fun searchPlanList(searchText: String) {
        Log.e("TEST", "$searchText")
        Log.e("TEST", "$planListData")
        // 검색 단어를 포함하는지 확인
        // 검색 API
    }

    // 필터로 인한 목록 변경 API
    private fun filterPlanList(filterType: Int) {
        when (filterType) {
            1 -> {
                Log.e("TEST", "1")
                Log.e("TEST", "$planListData")
            }

            2 -> {
                Log.e("TEST", "2")
                Log.e("TEST", "$planListData")
            }

            3 -> {
                Log.e("TEST", "3")
                Log.e("TEST", "$planListData")
            }

            else -> {
                Log.e("TEST", "오류")
            }
        }
    }

}