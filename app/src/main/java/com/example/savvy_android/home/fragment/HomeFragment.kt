package com.example.savvy_android.home.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentHomeBinding
import com.example.savvy_android.diary.activity.DiaryMake1Activity
import com.example.savvy_android.home.adapter.HomeAdapter
import com.example.savvy_android.home.adapter.HomeItemData
import com.example.savvy_android.utils.alarm.AlarmActivity
import com.example.savvy_android.utils.search.SearchActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeAdapter: HomeAdapter
    private var homeData = arrayListOf<HomeItemData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 알람 버튼 클릭 시 알람 페이지 연결
        binding.homeAlarm.setOnClickListener {
            val intent = Intent(context, AlarmActivity::class.java)
            startActivity(intent)
        }

        // 검색 버튼 클릭 시 검색 페이지 연결
        binding.homeSearch.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }

        // 홈화면 게시글 어뎁터
        homeAdapter = HomeAdapter(homeData)
        binding.homeRecycle.adapter = homeAdapter

        // 당겨서 새로고침 아이콘 색상
        binding.homeRefresh.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.main
            )
        )
        // 당겨서 새로 고침 할 때 진행할 작업
        binding.homeRefresh.setOnRefreshListener {
            // 리프레쉬 API
            binding.homeRefresh.isRefreshing = false
        }

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.homeAddFbtn.setOnClickListener {
            val intent = Intent(context, DiaryMake1Activity::class.java)
            intent.putExtra("isDiary",false)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val hasAlarm = true
        if (hasAlarm)
            binding.homeAlarm.setImageResource(R.drawable.ic_alarm_o)
        else
            binding.homeAlarm.setImageResource(R.drawable.ic_alarm_x)
    }
}