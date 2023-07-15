package com.example.savvy_android.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.savvy_android.databinding.ActivityPlaceAddBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.savvy_android.R
import com.example.savvy_android.fragment.PlaceStorageFragment
import com.example.savvy_android.fragment.SearchNewPlaceFragment

class PlaceAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceAddBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityPlaceAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "새로운 장소 검색"
                1 -> tab.text = "장소 저장함"
            }
        }.attach()

        // arrowLeft 아이콘 클릭하면 저장하지 않고 여행 계획서 페이지로 돌아가기
        binding.arrowLeft.setOnClickListener {
            finish()
        }
    }

    // TabLayout 관련
    inner class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment){

        private val fragmentList: Array<Fragment> = arrayOf(
            SearchNewPlaceFragment(),
            PlaceStorageFragment(),
        )

        // 전체 탭 개수 반환
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        // 해당 탭에 대한 프래그먼트 생성
        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }
}
