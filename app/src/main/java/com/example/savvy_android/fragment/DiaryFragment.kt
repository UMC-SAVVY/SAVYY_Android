package com.example.savvy_android.fragment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
<<<<<<<< HEAD:app/src/main/java/com/example/savvy_android/fragment/DiaryFragment.kt
import com.example.savvy_android.R
import com.example.savvy_android.activity.TravelPlanMakeActivity
import com.example.savvy_android.adapter.DiaryListAdapter
import com.example.savvy_android.data.DiaryItemData
import com.example.savvy_android.databinding.FragmentDiaryBinding
import com.example.savvy_android.touch.DiaryItemTouchCallback

class DiaryFragment : Fragment() {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var diaryListAdapter: DiaryListAdapter
    private var diaryListData = arrayListOf<DiaryItemData>()
    private val diaryTouchSimpleCallback = DiaryItemTouchCallback()
    private val itemTouchHelper = ItemTouchHelper(diaryTouchSimpleCallback)
========
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
>>>>>>>> ari:app/src/main/java/com/example/savvy_android/fragment/TravelPlanFragment.kt

    // 임시 데이터
    private var tmpPhotoList = arrayListOf(
        1, 2, 3, 4, 5
    )
    private var tmpDateList = arrayListOf(
        "2023.06.01",
        "2023.06.02",
        "2023.06.03",
        "2023.06.04",
        "2023.06.05",
    )
    // 여기 사이 dummy 데이터 삭제해야함.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
<<<<<<<< HEAD:app/src/main/java/com/example/savvy_android/fragment/DiaryFragment.kt
        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        // 알람 버튼 클릭시 알람 페이지 연결
        binding.planAlarm.setOnClickListener {
//            val intent = Intent(context, 알람 페이지 kotlin 파일)
//            startActivity(intent)
        }

        // 검색 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.diarySearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.diarySearchEdit.length() != 0
                binding.diarySearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.diarySearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Plan Data & Adapter
        diaryListAdapter =
            DiaryListAdapter(
                binding.diaryRecycle,
                diaryListData,
                requireActivity().supportFragmentManager
            )
        binding.diaryRecycle.adapter = diaryListAdapter


        // itemTouchHelper와 recyclerview 연결
        itemTouchHelper.attachToRecyclerView(binding.diaryRecycle)

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.diaryAddFbtn.setOnClickListener {
            val intent = Intent(context, TravelPlanMakeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
========
        binding = FragmentTravelPlanBinding.inflate(inflater, container, false)
>>>>>>>> ari:app/src/main/java/com/example/savvy_android/fragment/TravelPlanFragment.kt

        // 알람 존재 여부에 따른 알람 버튼 형태
        val hasAlarm = true
        if (hasAlarm)
            binding.planAlarm.setBackgroundResource(R.drawable.ic_alarm_o)
        else binding.planAlarm.setBackgroundResource(
            R.drawable.ic_alarm_x
        )

<<<<<<<< HEAD:app/src/main/java/com/example/savvy_android/fragment/DiaryFragment.kt
        // 임시 데이터 목록
        for (i: Int in 0 until tmpDateList.size) {
            diaryListAdapter.addPlan(
                DiaryItemData(
                    i,
                    "파리 여행",
                    tmpDateList[i],
                    100,
                    100,
                    tmpPhotoList[i],
                    R.drawable.ic_launcher_background,
                    true
========
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
>>>>>>>> ari:app/src/main/java/com/example/savvy_android/fragment/TravelPlanFragment.kt
                )
            )
        }

<<<<<<<< HEAD:app/src/main/java/com/example/savvy_android/fragment/DiaryFragment.kt
        // 검색 기능
        binding.diarySearchBtn.setOnClickListener {
            Log.e("TEST", "검색 버튼 눌림")
            searchDiaryList(binding.diarySearchEdit.text.toString())
========
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
>>>>>>>> ari:app/src/main/java/com/example/savvy_android/fragment/TravelPlanFragment.kt
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

    // 목록 검색 API
    private fun searchDiaryList(searchText: String) {
        Log.e("TEST", "$searchText")
        // 검색 단어를 포함하는지 확인
        // 검색 API
    }
}