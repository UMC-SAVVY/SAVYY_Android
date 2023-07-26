package com.example.savvy_android.utils.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.savvy_android.databinding.ActivitySearchBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.savvy_android.R

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivitySearchBinding.inflate(layoutInflater)
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
                0 -> tab.text = "제목 및 해시태그 검색"
                1 -> tab.text = "사용자 검색"
            }
        }.attach()

        // arrowLeft 아이콘 클릭하면 저장하지 않고 여행 계획서 페이지로 돌아가기
        binding.arrowLeft.setOnClickListener {
            finish()
        }
    }

    // TabLayout 관련
    inner class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

        private val fragmentList: Array<Fragment> = arrayOf(
            SearchDiaryFragment(),
            SearchUserFragment(),
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
