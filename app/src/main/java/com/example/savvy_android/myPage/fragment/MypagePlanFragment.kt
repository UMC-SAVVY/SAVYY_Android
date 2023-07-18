package com.example.savvy_android.myPage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.savvy_android.databinding.FragmentMypagePlanBinding
import com.example.savvy_android.plan.PlanItemData
import com.example.savvy_android.plan.adapter.PlanListAdapter

class MypagePlanFragment : Fragment() {
    private lateinit var binding: FragmentMypagePlanBinding
    private lateinit var planListAdapter: PlanListAdapter
    private var planListData = arrayListOf<PlanItemData>()

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
        binding = FragmentMypagePlanBinding.inflate(inflater, container, false)
        var userName = "나"

        // Plan Data & Adapter
        planListAdapter =
            PlanListAdapter(
                binding.planRecycle,
                planListData,
                userName,
                requireActivity().supportFragmentManager,
                false
            )
        binding.planRecycle.adapter = planListAdapter

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // 여기에 데이터 받아서 뿌리는거 넣어야함
    }
}