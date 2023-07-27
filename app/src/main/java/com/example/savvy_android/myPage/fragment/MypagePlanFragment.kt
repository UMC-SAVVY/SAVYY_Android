package com.example.savvy_android.myPage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.savvy_android.databinding.FragmentMypagePlanBinding
import com.example.savvy_android.plan.adapter.PlanListAdapter
import com.example.savvy_android.plan.data.list.PlanListResult

class MypagePlanFragment : Fragment() {
    private lateinit var binding: FragmentMypagePlanBinding
    private lateinit var planListAdapter: PlanListAdapter
    private var planListData = arrayListOf<PlanListResult>()

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // 여기에 데이터 받아서 뿌리는거 넣어야함
    }
}