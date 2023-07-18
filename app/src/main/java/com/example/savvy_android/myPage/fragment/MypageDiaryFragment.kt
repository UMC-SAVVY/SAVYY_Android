package com.example.savvy_android.myPage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentMypageDiaryBinding
import com.example.savvy_android.diary.adapter.DiaryListAdapter
import com.example.savvy_android.diary.data.DiaryItemData

class MypageDiaryFragment : Fragment() {
    private lateinit var binding: FragmentMypageDiaryBinding
    private lateinit var diaryListAdapter: DiaryListAdapter
    private var diaryListData = arrayListOf<DiaryItemData>()

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
        binding = FragmentMypageDiaryBinding.inflate(inflater, container, false)

        // Plan Data & Adapter
        diaryListAdapter =
            DiaryListAdapter(
                binding.diaryRecycle,
                diaryListData,
                requireActivity().supportFragmentManager,
                false
            )
        binding.diaryRecycle.adapter = diaryListAdapter

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