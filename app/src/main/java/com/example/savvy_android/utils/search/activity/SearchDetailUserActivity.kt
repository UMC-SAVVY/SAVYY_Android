package com.example.savvy_android.utils.search.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivitySearchOtherUserBinding
import com.example.savvy_android.myPage.fragment.MypageFragment

class SearchDetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchOtherUserBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivitySearchOtherUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 유저의 id
        userId = intent.getIntExtra("userId", 0)

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
    }

    // TabLayout 관련
    inner class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

        private val fragmentList: Array<Fragment> = arrayOf(
            MypageFragment(true, userId),
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
