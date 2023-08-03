package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.diary.adapter.Make1Adapter
import com.example.savvy_android.databinding.ActivityDiaryStep1Binding
import com.example.savvy_android.diary.dialog.DiaryStopDialogFragment
import com.example.savvy_android.diary.dialog.NextStepDialogFragment
import com.example.savvy_android.diary.dialog.PlanSelectDialogFragment
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.data.list.PlanListResponse
import com.example.savvy_android.plan.data.list.PlanListResult
import com.example.savvy_android.plan.service.PlanListService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiaryMake1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep1Binding
    private lateinit var diaryPlanListAdapter: Make1Adapter
    private var planListData = arrayListOf<PlanListResult>()
    private lateinit var valueAnimator: ValueAnimator
    private var isDiary: Boolean = true
    private lateinit var sharedPreferences: SharedPreferences
    private var currentType = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작된 fragment 정보 받기
        isDiary = intent.getBooleanExtra("isDiary", true)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // Diary에서 선택한 PLAN Data & Adapter
        diaryPlanListAdapter = Make1Adapter(binding.recyclerviewDiaryList, planListData)
        binding.recyclerviewDiaryList.adapter = diaryPlanListAdapter

        //seek bar 애니메이션
        binding.seekBar.max = 4000
        valueAnimator = ValueAnimator.ofInt(0, 1000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }
        valueAnimator.start()

        // 뒤로가기 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            val dialog = DiaryStopDialogFragment(isDiary)
            dialog.show(supportFragmentManager, "diaryStopDialog")
        }

        //다이어리 찾기 edit 버튼 활성화
        binding.diarySearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.diarySearchEdit.length() != 0
                binding.diarySearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.diarySearchBtn)

                if (!isEnableState) {
                    diaryPlanListAdapter.clearList() // 리스트 정보 초기화
                    currentType = 1
                    planListAPI(currentType, null)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep1Tv.text.toString())

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 29, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan2, 60, 62, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep1Tv.text = spannableString

    }

    override fun onResume() {
        super.onResume()
        // 생성될 때 목록 불러오기
        diaryPlanListAdapter.clearList() // 리스트 정보 초기화
        planListAPI(currentType, null)

        // 검색 기능
        binding.diarySearchBtn.setOnClickListener {
            diaryPlanListAdapter.checkmarkX(
                diaryPlanListAdapter.isClickedPosition,
                binding.recyclerviewDiaryList
            )
            diaryPlanListAdapter.isClickedPosition = -1 // 체크 정보 초기화
            diaryPlanListAdapter.clearList() // 리스트 정보 초기화
            currentType = 2
            planListAPI(currentType, binding.diarySearchEdit.text.toString())
        }

        binding.diaryNextBtn.setOnClickListener {
            if (diaryPlanListAdapter.isClickedPosition != -1) {
                val planSelectDialog = PlanSelectDialogFragment(
                    isDiary,
                    planListData[diaryPlanListAdapter.isClickedPosition].id
                )
                planSelectDialog.show(supportFragmentManager, "planSelectDialog")
            } else {
                val nextStepDialog = NextStepDialogFragment(isDiary)
                nextStepDialog.show(supportFragmentManager, "nextStepDialog")
            }
        }
    }

    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val buttonColor = if (able) {
            ContextCompat.getColor(button.context, R.color.main)
        } else {
            ContextCompat.getColor(button.context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }


    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiaryStopDialogFragment(isDiary)
        dialog.show(supportFragmentManager, "diaryStopDialog")
    }

    // 필터 & 검색으로 인한 목록 변경 API
    private fun planListAPI(
        type: Int,
        searchWord: String?,
    ) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        sharedPreferences =
            getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val planListService = retrofit.create(PlanListService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // GET 요청
        when (type) {
            // 목록 (나의 계획서)
            1 -> {
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
                                        diaryPlanListAdapter.addPlan(
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
                                        errorCodeList(
                                            errorCode = it.code,
                                            message = it.message,
                                            type = "DIARY",
                                            detailType = "MAKE1 MINE",
                                            intentData = null
                                        )
                                    }
                                }
                            } else {
                                Log.e(
                                    "DIARY",
                                    "[DIARY MAKE1 MINE] API 호출 실패 - 응답 코드: ${response.code()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                            // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                            Log.e(
                                "DIARY",
                                "[DIARY MAKE1 MINE] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                            )
                        }
                    })
            }
            // 검색
            2 -> {
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
                                        diaryPlanListAdapter.addPlan(
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
                                        errorCodeList(
                                            errorCode = it.code,
                                            message = it.message,
                                            type = "DIARY",
                                            detailType = "MAKE1 SEARCH",
                                            intentData = null
                                        )
                                    }
                                }
                            } else {
                                Log.e(
                                    "DIARY",
                                    "[DIARY MAKE1 SEARCH] API 호출 실패 - 응답 코드: ${response.code()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                            // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                            Log.e(
                                "DIARY",
                                "[DIARY MAKE1 SEARCH] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                            )
                        }
                    })
            }
        }
    }
}