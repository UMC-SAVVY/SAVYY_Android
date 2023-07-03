package com.example.savvy_android

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.savvy_android.databinding.ActivityPlaceAddBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PlaceAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceAddBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val tabTitleArray = arrayOf(
        "새로운 장소 검색",
        "장소 저장함"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityPlaceAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()

        // 선택된 탭과 선택되지 않은 글자색 둘 다 검정으로 설정
        binding.tabLayout.setTabTextColors(Color.BLACK, Color.BLACK)

        // arrowLeft 아이콘 클릭하면 저장하지 않고 여행 계획서 페이지로 돌아가기
        binding.arrowLeft.setOnClickListener {
            val intent = Intent(this, TravelPlanMakeActivity::class.java)
            startActivity(intent)
        }
    }
}

private const val NUM_TABS = 2

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    // 전체 탭 개수 반환
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    // 해당 탭에 대한 프래그먼트 생성
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchNewPlaceFragment()
            1 -> PlaceStorageFragment()
            else -> PlaceStorageFragment()
        }
    }
}
