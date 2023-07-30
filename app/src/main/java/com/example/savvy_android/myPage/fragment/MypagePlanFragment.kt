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
import com.example.savvy_android.plan.adapter.PlanListAdapter
import com.example.savvy_android.plan.data.list.PlanListResponse
import com.example.savvy_android.plan.data.list.PlanListResult
import com.example.savvy_android.plan.service.PlanListService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypagePlanFragment : Fragment() {
    private lateinit var binding: FragmentMypagePlanBinding
    private lateinit var planListAdapter: PlanListAdapter
    private var planListData = arrayListOf<PlanListResult>()
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
                planListData,
                nickname,
                requireActivity().supportFragmentManager,
                false
            )
        binding.planRecycle.adapter = planListAdapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        planListAdapter.clearList() // 리스트 정보 초기화

        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val planListService = retrofit.create(PlanListService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // GET 요청
        planListService.planListMine(token = accessToken)
            .enqueue(object : Callback<PlanListResponse> {
                override fun onResponse(
                    call: Call<PlanListResponse>,
                    response: Response<PlanListResponse>,
                ) {
                    if (response.isSuccessful) {
                        val planResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (planResponse?.isSuccess == true) {
                            for (result in planResponse.result) {
                                planListAdapter.addPlan(
                                    PlanListResult(
                                        id = result.id,
                                        title = result.title,
                                        updated_at = result.updated_at,
                                        nickname = null
                                    )
                                )
                            }
                        } else {
                            // 응답 에러 코드 분류
                            planResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "MINE",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "PLAN",
                            "[PLAN MINE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("PLAN", "[PLAN MINE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }
}