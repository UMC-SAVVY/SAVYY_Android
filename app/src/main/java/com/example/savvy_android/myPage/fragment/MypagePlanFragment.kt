package com.example.savvy_android.myPage.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentMypagePlanBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.myPage.data.UserPlannerResponse
import com.example.savvy_android.myPage.service.MyPageService
import com.example.savvy_android.plan.adapter.PlanListAdapter
import com.example.savvy_android.plan.data.list.PlanListResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypagePlanFragment(
    private val isSearching: Boolean,
    private val userId: Int,
) : Fragment() {
    private lateinit var binding: FragmentMypagePlanBinding
    private lateinit var planListAdapter: PlanListAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nickname: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMypagePlanBinding.inflate(inflater, container, false)

        sharedPreferences =
            activity?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        nickname = sharedPreferences.getString("USER_NICKNAME", null)!!

        // Plan Data & Adapter
        planListAdapter =
            PlanListAdapter(
                requireContext(),
                binding.planRecycle,
                nickname,
                requireActivity().supportFragmentManager,
                false
            )
        binding.planRecycle.adapter = planListAdapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        planListAdapter.clearSwipe() // 스와이프 고정 상태 해제

        if (isSearching)
            otherPlannerAPI(userId)
        else
            myPlannerAPI()
    }

    private fun myPlannerAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val myPageService = retrofit.create(MyPageService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // GET 요청
        myPageService.myPagePlanner(token = accessToken)
            .enqueue(object : Callback<UserPlannerResponse> {
                override fun onResponse(
                    call: Call<UserPlannerResponse>,
                    response: Response<UserPlannerResponse>,
                ) {
                    if (response.isSuccessful) {
                        val myPageResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (myPageResponse?.isSuccess == true) {
                            if (myPageResponse.result.amount_planner > 0) {
                                val tempList = arrayListOf<PlanListResult>()
                                for (result in myPageResponse.result.planner) {
                                    tempList.add(
                                        PlanListResult(
                                            id = result.id,
                                            title = result.title,
                                            updated_at = result.updated_at,
                                            nickname = null
                                        )
                                    )
                                }
                                planListAdapter.submitList(tempList)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            myPageResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "MYPAGE PLAN",
                                    detailType = "MINE",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "MYPAGE PLAN",
                            "[MYPAGE PLAN MINE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserPlannerResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("MYPAGE PLAN", "[MYPAGE PLAN MINE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    private fun otherPlannerAPI(userId: Int) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val myPageService = retrofit.create(MyPageService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // GET 요청
        myPageService.otherPagePlanner(token = accessToken, userId = userId)
            .enqueue(object : Callback<UserPlannerResponse> {
                override fun onResponse(
                    call: Call<UserPlannerResponse>,
                    response: Response<UserPlannerResponse>,
                ) {
                    if (response.isSuccessful) {
                        val userPageResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (userPageResponse?.isSuccess == true) {
                            if (userPageResponse.result.amount_planner > 0) {
                                val tempList = arrayListOf<PlanListResult>()
                                for (result in userPageResponse.result.planner) {
                                    tempList.add(
                                        PlanListResult(
                                            id = result.id,
                                            title = result.title,
                                            updated_at = result.updated_at,
                                            nickname = null
                                        )
                                    )
                                }
                                planListAdapter.submitList(tempList)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            userPageResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "MYPAGE PLAN",
                                    detailType = "OTHER",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "MYPAGE PLAN",
                            "[MYPAGE PLAN OTHER] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserPlannerResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("MYPAGE PLAN", "[MYPAGE PLAN OTHER] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }
}