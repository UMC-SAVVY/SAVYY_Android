package com.example.savvy_android.myPage.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentMypageBinding
import com.example.savvy_android.myPage.activity.MypageConditionActivity
import com.example.savvy_android.myPage.activity.MypagePlaceActivity
import com.example.savvy_android.myPage.activity.ProfileSettingActivity
import com.example.savvy_android.myPage.dialog.MypageLogoutDialogFragment
import com.example.savvy_android.myPage.dialog.MypageWithdrawalDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

class MypageFragment : Fragment() {
    private lateinit var binding: FragmentMypageBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var isSetting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 설정 버튼 클릭 이벤트
        binding.mypageSettingIc.setOnClickListener {
            if (isSetting) {    // 설정 화면 -> 초기화면으로 변경
                binding.mypageSettingIc.setImageResource(R.drawable.ic_setting)
                binding.mypageDataLayout.visibility = View.VISIBLE
                binding.mypageSettingLayout.visibility = View.INVISIBLE
                isSetting = false
            } else {    // 마이페이지 초기 화면 -> 설정화면으로 변경
                binding.mypageSettingIc.setImageResource(R.drawable.ic_reset)
                binding.mypageDataLayout.visibility = View.INVISIBLE
                binding.mypageSettingLayout.visibility = View.VISIBLE
                isSetting = true
            }
        }

        // 프로필 편집 클릭 이벤트
        binding.mypageSetting1.setOnClickListener {
            val intent = Intent(context, ProfileSettingActivity::class.java)
            intent.putExtra("isMyPage", true)
            startActivity(intent)
        }

        // 장소 저장함 편집 클릭 이벤트
        binding.mypageSetting2.setOnClickListener {
            val intent = Intent(context, MypagePlaceActivity::class.java)
            startActivity(intent)
        }

        // 이용약관 클릭 이벤트
        binding.mypageSetting3.setOnClickListener {
            val intent = Intent(context, MypageConditionActivity::class.java)
            startActivity(intent)
        }

        // 회원탈퇴 클릭 이벤트
        binding.mypageSetting4.setOnClickListener {
            val dialog = MypageWithdrawalDialogFragment()
            dialog.show(requireFragmentManager(), "withdrawalDialog")
        }

        // 로그아웃 클릭 이벤트
        binding.mypageSetting5.setOnClickListener {
            val dialog = MypageLogoutDialogFragment()
            dialog.show(requireFragmentManager(), "logoutDialog")
        }

        viewPagerAdapter = ViewPagerAdapter(requireActivity())
        binding.mypageViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(
            binding.mypageTabLayout,
            binding.mypageViewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "다이어리"
                1 -> tab.text = "계획서"
            }
        }.attach()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val profileData = R.drawable.ic_profile_default
        val nameData = "닉네임"
        val introData = "소개글입니다요"

        // 프로필 사진 클릭 이벤트
        Glide.with(this)
            .load(profileData)
            .into(binding.mypageProfileIv)


        // 프로필 닉네임
        binding.mypageNicknameTv.text = nameData

        // 각 내용 카운트
        binding.mypageLikeCount.text = "100"    // 좋아요
        binding.mypagePlanCount.text = "100"    // 계획서
        binding.mypageDiaryCount.text = "100"    // 다이어리

        // 소개글
        if (introData.isNotEmpty()) {
            binding.mypageIntroTv.visibility = View.VISIBLE
            binding.mypageIntroTv.text = introData
        } else {
            binding.mypageIntroTv.visibility = View.GONE
        }
    }

    // TabLayout 관련
    inner class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
        private val fragmentList: Array<Fragment> = arrayOf(
            MypageDiaryFragment(),
            MypagePlanFragment(),
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