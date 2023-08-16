package com.example.savvy_android.init

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityMainBinding
import com.example.savvy_android.diary.fragment.DiaryFragment
import com.example.savvy_android.home.fragment.HomeFragment
import com.example.savvy_android.myPage.fragment.MypageFragment
import com.example.savvy_android.plan.fragment.PlanFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // DiaryFragment를 보여줄 것인지 확인하고 보여주기
        if (intent.getBooleanExtra("SHOW_DIARY_FRAGMENT", false)) {
            setFragment(DiaryFragment())
            binding.mainBottomNavi.selectedItemId =
                R.id.diary // Bottom Navigation에서 DiaryFragment 선택 상태로 변경
        } else {
            setFragment(HomeFragment()) // 일반적인 경우는 HomeFragment를 보여줌
        }

        // 모든 FragmentActivity, 그리고 그것의 subclass (AppCompatActivity 와 같은) 는
        // 'getSupportFragmentManager' 로 FragmentManager 에 접근 가능
        // .commitAllowingStateLoss() 는 활동 상태를 저장 후에 실행 되도록 설정

        //원래 코드 - merge 하면서 말 하기
        //Fragment를 초기화 할 때마다 기본으로 HomeFragment 표시
        setBottomNavi()
    }

    private fun setBottomNavi() {
        // menu.bottom_navi.xml 의 item 들의 id 값에 따라 변경 되도록
        // 'setOnItemSelectedListener' 는 item 이 선택 되었을 때
        binding.mainBottomNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    setFragment(HomeFragment())
                    // setOnItemSelectedListener 선택을 boolean 형식으로 반환
                    return@setOnItemSelectedListener true
                }

                R.id.plan -> {
                    setFragment(PlanFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.diary -> {
                    setFragment(DiaryFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.mypage -> {
                    setFragment(MypageFragment(false, 0))
                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        // 바뀐 fragment 로 commit()
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, fragment).commit()
    }
}