package com.example.savvy_android.myPage.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentMypageBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.init.SplashActivity
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.myPage.activity.MypageBlockActivity
import com.example.savvy_android.myPage.activity.MypageConditionActivity
import com.example.savvy_android.myPage.activity.MypageLikeActivity
import com.example.savvy_android.myPage.activity.MypagePlaceActivity
import com.example.savvy_android.myPage.activity.ProfileSettingActivity
import com.example.savvy_android.myPage.data.UserPageResponse
import com.example.savvy_android.myPage.dialog.MypageLogoutDialogFragment
import com.example.savvy_android.myPage.dialog.MypageWithdrawalDialogFragment
import com.example.savvy_android.myPage.service.MyPageService
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageFragment(
    private var isSearching: Boolean,
    private var userId: Int,
) : Fragment() {
    private lateinit var binding: FragmentMypageBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var isPause = false
    private var profileResource: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireContext().getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)

        if (!isSearching) {
            // 뒤로 가기 버튼 클릭 이벤트
            binding.mypageArrowIc.setOnClickListener {
                // 설정 화면 -> 초기 화면
                binding.mypageArrowIc.visibility = View.INVISIBLE
                binding.mypageSettingIc.visibility = View.VISIBLE
                binding.mypageDataLayout.visibility = View.VISIBLE
                binding.mypageSettingLayout.visibility = View.INVISIBLE
            }

            // 설정 버튼 클릭 이벤트
            binding.mypageSettingIc.setOnClickListener {
                // 마이페이지 초기 화면 -> 설정 화면
                binding.mypageArrowIc.visibility = View.VISIBLE
                binding.mypageSettingIc.visibility = View.INVISIBLE
                binding.mypageDataLayout.visibility = View.INVISIBLE
                binding.mypageSettingLayout.visibility = View.VISIBLE
            }

            // 프로필 편집 클릭 이벤트
            binding.mypageSettingProfile.setOnClickListener {
                val intent = Intent(context, ProfileSettingActivity::class.java)
                intent.putExtra("isMyPage", true)
                intent.putExtra("pic_url", profileResource.ifEmpty { "" })
                intent.putExtra("nickname", binding.mypageNicknameTv.text.toString())
                intent.putExtra("intro", binding.mypageIntroTv.text.ifEmpty { "" })
                startActivity(intent)
            }

            // 장소 저장함 편집 클릭 이벤트
            binding.mypageSettingPlace.setOnClickListener {
                val intent = Intent(context, MypagePlaceActivity::class.java)
                startActivity(intent)
            }

            // 다이어리 좋아요 목록 클릭 이벤트
            binding.mypageSettingLike.setOnClickListener {
                val intent = Intent(context, MypageLikeActivity::class.java)
                startActivity(intent)
            }

            // 차단 목록 관리 클릭 이벤트
            binding.mypageSettingBlock.setOnClickListener {
                val intent = Intent(context, MypageBlockActivity::class.java)
                startActivity(intent)
            }

            // 이용약관 클릭 이벤트
            binding.mypageSettingCondition.setOnClickListener {
                val intent = Intent(context, MypageConditionActivity::class.java)
                startActivity(intent)
            }

            // 회원탈퇴 클릭 이벤트
            binding.mypageSettingWithdrawal.setOnClickListener {
                val dialog = MypageWithdrawalDialogFragment()

                dialog.setButtonClickListener(object :
                    MypageWithdrawalDialogFragment.OnButtonClickListener {
                    override fun onDialogPlanBtnOClicked() {
                        // 연결 끊기
                        UserApiClient.instance.unlink { error ->
                            if (error != null) {
                                showToast("회원 탈퇴를 실패했습니다. 다시 시도해주세요.")
                            } else {
                                val editor = sharedPreferences.edit()
                                editor.putString("SERVER_TOKEN_KEY", null)
                                editor.putString("USER_NICKNAME", null)
                                editor.apply()

                                // 초기 화면으로 이동
                                val intent = Intent(requireContext(), SplashActivity::class.java)
                                // 이전에 존재하던 모든 acitivty 종료
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                                showToast("회원 탈퇴를 성공했습니다.")
                            }
                        }
                    }

                    override fun onDialogPlanBtnXClicked() {
                    }
                })
                dialog.show(childFragmentManager, "withdrawalDialog")
            }

            // 로그아웃 클릭 이벤트
            binding.mypageSettingLogout.setOnClickListener {
                val editor = sharedPreferences.edit()
                editor.putString("SERVER_TOKEN_KEY", null)
                editor.putString("USER_NICKNAME", null)
                editor.apply()
                val dialog = MypageLogoutDialogFragment()
                dialog.show(childFragmentManager, "logoutDialog")
            }
        } else {
            binding.mypageArrowIc.visibility = View.VISIBLE
            binding.mypageSettingIc.visibility = View.INVISIBLE
            // 뒤로 가기 버튼 클릭 이벤트
            binding.mypageArrowIc.setOnClickListener {
                activity?.finish()
            }
        }

        viewPagerAdapter = ViewPagerAdapter(requireActivity())
        binding.mypageViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(
            binding.mypageTabLayout,
            binding.mypageViewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "계획서"
                1 -> tab.text = "다이어리"
            }
        }.attach()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        isPause = false

        Log.e("TEST", "onResume")

        if (isSearching) {
            Log.e("TEST", "isSearching=true")
            otherUserPageAPI()
        } else {
            myPageAPI()
            Log.e("TEST", "isSearching=false")
        }
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    // TabLayout 관련
    inner class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
        private val fragmentList: Array<Fragment> = arrayOf(
            MypagePlanFragment(isSearching, userId),
            MypageDiaryFragment(isSearching, userId),
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

    private fun myPageAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val myPageService = retrofit.create(MyPageService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        myPageService.myPageInfo(token = accessToken)
            .enqueue(object : Callback<UserPageResponse> {
                override fun onResponse(
                    call: Call<UserPageResponse>,
                    response: Response<UserPageResponse>,
                ) {
                    if (response.isSuccessful) {
                        val myPageResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (myPageResponse?.isSuccess == true && myPageResponse.code == 1000) {
                            if (!isPause) {
                                val result = myPageResponse.result
                                binding.mypageNicknameTv.text = result.nickname
                                binding.mypageLikeCount.text = result.likes.toString()
                                binding.mypagePlanCount.text = result.amount_planner.toString()
                                binding.mypageDiaryCount.text = result.amount_diary.toString()

                                // 소개글
                                if (result.intro.isNotEmpty()) {
                                    binding.mypageIntroTv.visibility = View.VISIBLE
                                    binding.mypageIntroTv.text = result.intro
                                } else {
                                    binding.mypageIntroTv.visibility = View.GONE
                                    binding.mypageIntroTv.text = ""
                                }

                                // 프로필 사진
                                if (!result.pic_url.isNullOrEmpty()) {
                                    profileResource = result.pic_url
                                    Glide.with(this@MypageFragment)
                                        .load(result.pic_url)
                                        .into(binding.mypageProfileIv)
                                } else {
                                    profileResource = ""
                                    val profileData = R.drawable.ic_profile_default
                                    Glide.with(this@MypageFragment)
                                        .load(profileData)
                                        .into(binding.mypageProfileIv)
                                }
                            }
                        } else {
                            // 응답 에러 코드 분류
                            myPageResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "MYPAGE INFO",
                                    detailType = "MINE",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "MYPAGE INFO",
                            "[MYPAGE INFO MINE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserPageResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("MYPAGE INFO", "[MYPAGE INFO MINE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    private fun otherUserPageAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val myPageService = retrofit.create(MyPageService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        myPageService.otherPageInfo(token = accessToken, userId = userId, isSearching = isSearching)
            .enqueue(object : Callback<UserPageResponse> {
                override fun onResponse(
                    call: Call<UserPageResponse>,
                    response: Response<UserPageResponse>,
                ) {
                    if (response.isSuccessful) {
                        val myPageResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (myPageResponse?.isSuccess == true && myPageResponse.code == 1000) {
                            val result = myPageResponse.result
                            binding.mypageNicknameTv.text = result.nickname
                            binding.mypageLikeCount.text = result.likes.toString()
                            binding.mypagePlanCount.text = result.amount_planner.toString()
                            binding.mypageDiaryCount.text = result.amount_diary.toString()
                            // 소개글
                            if (result.intro.isNotEmpty()) {
                                binding.mypageIntroTv.visibility = View.VISIBLE
                                binding.mypageIntroTv.text = result.intro
                            } else {
                                binding.mypageIntroTv.visibility = View.GONE
                            }

                            // 프로필 사진
                            if (!result.pic_url.isNullOrEmpty()) {
                                Glide.with(this@MypageFragment)
                                    .load(result.pic_url)
                                    .into(binding.mypageProfileIv)
                            } else {
                                val profileData = R.drawable.ic_profile_default
                                Glide.with(this@MypageFragment)
                                    .load(profileData)
                                    .into(binding.mypageProfileIv)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            myPageResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "MYPAGE INFO",
                                    detailType = "OTHER",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "MYPAGE INFO",
                            "[MYPAGE INFO OTHER] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserPageResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("MYPAGE INFO", "[MYPAGE INFO OTHER] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    // 토스트 메시지
    private fun showToast(message: String) {
        val toastBinding =
            LayoutToastBinding.inflate(LayoutInflater.from(requireContext()))
        toastBinding.toastMessage.text = message
        val toast = Toast(requireContext())
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}