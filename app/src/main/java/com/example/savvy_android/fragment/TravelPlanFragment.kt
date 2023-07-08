package com.example.savvy_android.fragment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.savvy_android.touch.PlanItemTouchCallback
import com.example.savvy_android.R
import com.example.savvy_android.activity.TravelPlanMakeActivity
import com.example.savvy_android.adapter.TravelPlanListAdapter
import com.example.savvy_android.data.TravelPlanItemData
import com.example.savvy_android.databinding.FragmentTravelPlanBinding


class TravelPlanFragment : Fragment() {
    private lateinit var binding: FragmentTravelPlanBinding
    private lateinit var travelPlanListAdapter: TravelPlanListAdapter
    private var planListData = arrayListOf<TravelPlanItemData>()
    private val itemTouchSimpleCallback = PlanItemTouchCallback()
    private val itemTouchHelper = ItemTouchHelper(itemTouchSimpleCallback)

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTravelPlanBinding.inflate(inflater, container, false)

        // 알람 존재 여부에 따른 알람 버튼 형태
        val hasAlarm = true
        if (hasAlarm)
            binding.planAlarm.setBackgroundResource(R.drawable.ic_alarm_o)
        else binding.planAlarm.setBackgroundResource(
            R.drawable.ic_alarm_x
        )

        // 알람 버튼 클릭시 알람 페이지 연결
        binding.planAlarm.setOnClickListener {
//            val intent = Intent(context, 알람 페이지 kotlin 파일)
//            startActivity(intent)
        }

        // 검색 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.newPlaceSearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.newPlaceSearchEdit.length() != 0
                binding.newPlaceSearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.newPlaceSearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Plan Data & Adapter
        travelPlanListAdapter =
            TravelPlanListAdapter(planListData, "나", requireActivity().supportFragmentManager)
        binding.planRecycle.adapter = travelPlanListAdapter

        // 임시 데이터 목록
        for (i: Int in 0 until tmpDateList.size) {
            travelPlanListAdapter.run {
                addPlan(
                    TravelPlanItemData(
                        i,
                        "하와이 여행 ${i + 1}",
                        tmpDateList[i],
                        tmpNameList[i],
                    )
                )
            }
        }

        // 전체보기 클릭 이벤트
        binding.planFilterBtn1.setOnClickListener {
            binding.planFilterBtn1.background
        }

        // 나의 계획서 클릭 이벤트
        binding.planFilterBtn1.setOnClickListener {

        }

        // 스크랩 클릭 이벤트
        binding.planFilterBtn1.setOnClickListener {

        }

        // itemTouchHelper와 recyclerview 연결
        itemTouchHelper.attachToRecyclerView(binding.planRecycle)

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.planAddFbtn.setOnClickListener {
            val intent = Intent(context, TravelPlanMakeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
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
}