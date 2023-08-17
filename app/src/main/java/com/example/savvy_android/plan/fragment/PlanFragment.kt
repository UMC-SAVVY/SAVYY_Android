package com.example.savvy_android.plan.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.savvy_android.R
import com.example.savvy_android.plan.activity.PlanMakeActivity
import com.example.savvy_android.plan.adapter.PlanListAdapter
import com.example.savvy_android.databinding.FragmentPlanBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.PlanItemTouchCallback
import com.example.savvy_android.plan.data.list.PlanListResponse
import com.example.savvy_android.plan.data.list.PlanListResult
import com.example.savvy_android.plan.service.PlanListService
import com.example.savvy_android.utils.LoadingDialogFragment
import com.example.savvy_android.utils.alarm.AlarmActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PlanFragment : Fragment() {
    private lateinit var binding: FragmentPlanBinding
    private lateinit var planListAdapter: PlanListAdapter
    private var planListData = arrayListOf<PlanListResult>()
    private val planTouchSimpleCallback = PlanItemTouchCallback()
    private val itemTouchHelper = ItemTouchHelper(planTouchSimpleCallback)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nickname: String
    private var currentType = 1
    private var isPause = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlanBinding.inflate(inflater, container, false)

        sharedPreferences =
            activity?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        nickname = sharedPreferences.getString("USER_NICKNAME", null)!!

        // 알람 버튼 클릭시 알람 페이지 연결
        binding.planAlarm.setOnClickListener {
            val intent = Intent(context, AlarmActivity::class.java)
            startActivity(intent)
        }

        // 검색 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.planSearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.planSearchEdit.length() != 0
                binding.planSearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.planSearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Plan Data & Adapter
        binding.planRecycle.itemAnimator = null
        planListAdapter =
            PlanListAdapter(
                requireContext(),
                binding.planRecycle,
                planListData,
                nickname,
                requireActivity().supportFragmentManager,
                true
            )
        binding.planRecycle.adapter = planListAdapter


        // itemTouchHelper와 recyclerview 연결
        itemTouchHelper.attachToRecyclerView(binding.planRecycle)

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.planAddFbtn.setOnClickListener {
            val intent = Intent(context, PlanMakeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        isPause = false

        // 생성될 때 목록 불러오기
        planListAdapter.clearList() // 리스트 정보 초기화
        planListAPI(currentType, null)

        val hasAlarm = true
        if (hasAlarm)
            binding.planAlarm.setImageResource(R.drawable.ic_alarm_o)
        else
            binding.planAlarm.setImageResource(R.drawable.ic_alarm_x)


        // 검색 기능
        binding.planSearchBtn.setOnClickListener {
            planListAdapter.clearList() // 리스트 정보 초기화
            planListAPI(4, binding.planSearchEdit.text.toString())
        }

        // 전체보기 클릭 이벤트
        binding.planFilterBtn1.setOnClickListener {
            btnClickColors(true, binding.planFilterBtn1)
            btnClickColors(false, binding.planFilterBtn2)
            btnClickColors(false, binding.planFilterBtn3)
            planListAdapter.clearList() // 리스트 정보 초기화
            currentType = 1
            planListAPI(currentType, null)
        }

        // 나의 계획서 클릭 이벤트
        binding.planFilterBtn2.setOnClickListener {
            btnClickColors(false, binding.planFilterBtn1)
            btnClickColors(true, binding.planFilterBtn2)
            btnClickColors(false, binding.planFilterBtn3)
            planListAdapter.clearList() // 리스트 정보 초기화
            currentType = 2
            planListAPI(currentType, null)
        }

        // 스크랩 클릭 이벤트
        binding.planFilterBtn3.setOnClickListener {
            btnClickColors(false, binding.planFilterBtn1)
            btnClickColors(false, binding.planFilterBtn2)
            btnClickColors(true, binding.planFilterBtn3)
            planListAdapter.clearList() // 리스트 정보 초기화
            currentType = 3
            planListAPI(currentType, null)
        }

    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    // 클릭 가능 여부에 따른 button 배경 변경 함수
    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val context: Context = requireContext()
        val buttonColor = if (able) {
            ContextCompat.getColor(context, R.color.main)
        } else {
            ContextCompat.getColor(context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }

    // 필터 버튼들 클릭시 버튼 변경
    private fun btnClickColors(isClick: Boolean, button: AppCompatButton) {
        val context: Context = requireContext()
        if (isClick) {
            // 버튼 배경
            button.setBackgroundResource(R.drawable.btn_radius4)
            button.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main))
            button.setTextColor(ContextCompat.getColor(context, R.color.white)) // 버튼 텍스트 color
            button.typeface =
                ResourcesCompat.getFont(context, R.font.pretendard_bold)  // 버튼 텍스트 font
        } else {
            // 버튼 배경
            button.setBackgroundResource(R.drawable.btn_radius4)
            button.backgroundTintList = null
            button.setTextColor(ContextCompat.getColor(context, R.color.black)) // 버튼 텍스트 color
            button.typeface =
                ResourcesCompat.getFont(context, R.font.pretendard_regular)   // 버튼 텍스트 font
        }
    }

    // 필터 & 검색으로 인한 목록 변경 API
    private fun planListAPI(
        type: Int,
        searchWord: String?,
    ) {
        var isFinish = false
        var isLoading = false
        val dialog = LoadingDialogFragment()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinish && !isPause) {
                dialog.show(childFragmentManager, "LoadingDialog")
                isLoading = true
            }
        }, 500)

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
        when (type) {
            // 목록 (전체보기)
            1 -> {
                planListService.planListAll(token = accessToken)
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
                                                nickname = result.nickname
                                            )
                                        )
                                    }
                                } else {
                                    // 응답 에러 코드 분류
                                    planResponse?.let {
                                        context?.errorCodeList(
                                            errorCode = it.code,
                                            message = it.message,
                                            type = "PLAN",
                                            detailType = "ALL",
                                            intentData = null
                                        )
                                    }
                                }
                            } else {
                                Log.e(
                                    "PLAN",
                                    "[PLAN ALL] API 호출 실패 - 응답 코드: ${response.code()}"
                                )
                            }

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }

                        override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                            // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                            Log.e("PLAN", "[PLAN ALL] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }
                    })
            }
            // 목록 (나의 계획서)
            2 -> {
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
                                            type = "PLAN",
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

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }

                        override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                            // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                            Log.e("PLAN", "[PLAN MINE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }
                    })
            }
            // 목록 (스크랩)
            3 -> {
                planListService.planListScrap(token = accessToken)
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
                                                nickname = result.nickname
                                            )
                                        )
                                    }
                                } else {
                                    // 응답 에러 코드 분류
                                    planResponse?.let {
                                        context?.errorCodeList(
                                            errorCode = it.code,
                                            message = it.message,
                                            type = "PLAN",
                                            detailType = "SCRAP",
                                            intentData = null
                                        )
                                    }
                                }
                            } else {
                                Log.e(
                                    "PLAN",
                                    "[PLAN SCRAP] API 호출 실패 - 응답 코드: ${response.code()}"
                                )
                            }

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }

                        override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                            // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                            Log.e("PLAN", "[PLAN SCRAP] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }
                    })
            }
            // 검색
            4 -> {
                planListService.planListSearch(token = accessToken, word = searchWord!!)
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
                                                nickname = result.nickname
                                            )
                                        )
                                    }
                                } else {
                                    // 응답 에러 코드 분류
                                    planResponse?.let {
                                        context?.errorCodeList(
                                            errorCode = it.code,
                                            message = it.message,
                                            type = "PLAN",
                                            detailType = "SEARCH",
                                            intentData = null
                                        )
                                    }
                                }
                            } else {
                                Log.e(
                                    "PLAN",
                                    "[PLAN SEARCH] API 호출 실패 - 응답 코드: ${response.code()}"
                                )
                            }

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }

                        override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                            // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                            Log.e("PLAN", "[PLAN SEARCH] API 호출 실패 - 네트워크 연결 실패: ${t.message}")


                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }
                    })
            }
        }
    }
}