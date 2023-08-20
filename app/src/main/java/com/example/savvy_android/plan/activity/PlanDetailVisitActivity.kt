package com.example.savvy_android.plan.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityPlanDetailVisitBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.plan.adapter.DetailDateAdapter
import com.example.savvy_android.plan.data.PlanDetailResponse
import com.example.savvy_android.plan.data.PlanMakeRequest
import com.example.savvy_android.plan.data.PlanMakeResponse
import com.example.savvy_android.plan.data.Timetable
import com.example.savvy_android.plan.dialog.PlanGetDialogFragment
import com.example.savvy_android.plan.dialog.PlanScrapDialogFragment
import com.example.savvy_android.plan.service.PlanDetailService
import com.example.savvy_android.plan.service.PlanIDRequest
import com.example.savvy_android.plan.service.PlanMakeService
import com.example.savvy_android.plan.service.PlanScrapService
import com.example.savvy_android.utils.BottomSheetOtherDialogFragment
import com.example.savvy_android.utils.report.ReportActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlanDetailVisitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanDetailVisitBinding
    private lateinit var viewDateAdapter: DetailDateAdapter
    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의
    private var planID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityPlanDetailVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)

        planID = intent.getIntExtra("planID", 0)

        Log.d("PlanDetailVisitActivity", "planID: $planID")

        val timetableList: MutableList<Timetable> = mutableListOf()
        viewDateAdapter = DetailDateAdapter(timetableList, false)
        binding.recyclerviewViewDate.adapter = viewDateAdapter
        binding.recyclerviewViewDate.layoutManager = LinearLayoutManager(this)

        // 계획서 가져오기 클릭
        binding.planGetBtn.setOnClickListener {
            val dialog = PlanGetDialogFragment()
            dialog.setButtonClickListener(object : PlanGetDialogFragment.OnButtonClickListener {
                override fun onDialogCopyClicked() {
                    planCopyAPI(binding, viewDateAdapter)
                }

                override fun onDialogCancelClicked() {

                }
            })
            dialog.show(supportFragmentManager, "planGetDialog")
        }

        // 계획서 스크랩하기 클릭
        binding.planScrapBtn.setOnClickListener {
            val dialog = PlanScrapDialogFragment()
            dialog.setButtonClickListener(object : PlanScrapDialogFragment.OnButtonClickListener {
                override fun onDialogScrapClicked() {
                    planScrapAPI(planId = planID)
                }

                override fun onDialogCancelClicked() {

                }
            })
            dialog.show(supportFragmentManager, "planScrapDialog")
        }


        // 뒤로 가기 버튼 클릭
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // 옵션 관련 (다른사람이 작성한 다이어리)
        val bottomSheetOther = BottomSheetOtherDialogFragment()
        bottomSheetOther.setButtonClickListener(object :
            BottomSheetOtherDialogFragment.OnButtonClickListener {
            override fun onDialogReportClicked() {
                val intent = Intent(this@PlanDetailVisitActivity, ReportActivity::class.java)
                intent.putExtra("planID", planID)
                startActivity(intent)
            }
        })


        //option 클릭하면 bottom sheet
        binding.optionBtn.setOnClickListener {
            bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")

        }
        planDetailAPI(planID, binding)

    }

    private fun planDetailAPI(planId: Int, binding: ActivityPlanDetailVisitBinding) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val planDetailService = retrofit.create(PlanDetailService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!
        planDetailService.planDetail(serverToken, planId.toString()).enqueue(object :
            Callback<PlanDetailResponse> {
            override fun onResponse(
                call: Call<PlanDetailResponse>,
                response: Response<PlanDetailResponse>,
            ) {
                if (response.isSuccessful) {
                    val planDetailResponse = response.body()
                    val isSuccess = planDetailResponse?.isSuccess
                    val code = planDetailResponse?.code
                    val message = planDetailResponse?.message
                    if (planDetailResponse != null && planDetailResponse.isSuccess) {
                        val planDetailResult = planDetailResponse.result
                        // planDetailResult에 들어있는 데이터를 사용하여 작업
                        binding.travelPlanViewTitleTv.text = planDetailResult.title
                        binding.travelPlanViewUserTv.text = planDetailResult.nickname
                        binding.travelPlanViewUpdateTv.text = planDetailResult.updated_at
                        if (!planDetailResult.pic_url.isNullOrEmpty())
                            Glide.with(this@PlanDetailVisitActivity)
                                .load(planDetailResult.pic_url)
                                .into(binding.profile)

                        viewDateAdapter.addAllItems(planDetailResponse.result.timetable)

                        planID = planDetailResult.id

                    } else {
                        Log.d(
                            "PlanDetailActivity",
                            "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message"
                        )
                    }
                } else {
                    val errorCode = response.code()
                    Log.e("PlanDetailActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<PlanDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.e("PlanDetailActivity", "통신 실패 - ${t.message}")
            }
        })
    }

    private fun planCopyAPI(binding: ActivityPlanDetailVisitBinding, adapter: DetailDateAdapter) {
        // 만들기 완료 버튼 클릭 이벤트
        val titleText = binding.travelPlanViewTitleTv.text.toString()
        val memoText = intent.getStringExtra("memoText") ?: ""
        val nickname = ""
        val currentTime = ""
        if (titleText.isNotEmpty()) {
            val timetableList = adapter.getDataList()
            val planMakeRequest = PlanMakeRequest(
                null, memoText, nickname,
                timetableList, titleText, currentTime
            )

            // 서버 주소
            val serverAddress = getString(R.string.serverAddress)

            val retrofit = Retrofit.Builder()
                .baseUrl(serverAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val planMakeService = retrofit.create(PlanMakeService::class.java)

            val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

            // 서버에 데이터 전송
            planMakeService.planMake(serverToken, planMakeRequest).enqueue(object :
                Callback<PlanMakeResponse> {
                override fun onResponse(
                    call: Call<PlanMakeResponse>,
                    response: Response<PlanMakeResponse>,
                ) {
                    if (response.isSuccessful) {
                        val planMakeResponse = response.body()
                        val isSuccess = planMakeResponse?.isSuccess
                        val code = planMakeResponse?.code
                        val message = planMakeResponse?.message
                        if (planMakeResponse != null && planMakeResponse.isSuccess) {
                            // 전송 성공
                            finish()
                            showToast("계획서를 성공적으로 가져왔습니다")
                        } else {
                            // 전송 실패
                            Log.d(
                                "PlanDetailVisitActivity",
                                "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message"
                            )
                            showToast("계획서를 가져오는데 실패했습니다.")
                        }
                    } else {
                        // 서버 오류
                        val errorCode = response.code()
                        Log.d("PlanDetailVisitActivity", "서버 오류 - $errorCode")
                        showToast("계획서를 가져오는데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<PlanMakeResponse>, t: Throwable) {
                    // 통신 실패
                    Log.d("PlanDetailVisitActivity", "통신 실패 - ${t.message}")
                    showToast("계획서를 가져오는데 실패했습니다.")
                }
            })
        }
    }

    private fun planScrapAPI(planId: Int) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val planMakeService = retrofit.create(PlanScrapService::class.java)
        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        planMakeService.planScrap(serverToken, planID = PlanIDRequest(planId)).enqueue(object :
            Callback<PlanMakeResponse> {
            override fun onResponse(
                call: Call<PlanMakeResponse>,
                response: Response<PlanMakeResponse>,
            ) {
                Log.e("TEST", "${response.body()}")
                if (response.isSuccessful) {
                    val planMakeResponse = response.body()
                    val isSuccess = planMakeResponse?.isSuccess
                    val code = planMakeResponse?.code
                    val message = planMakeResponse?.message
                    if (planMakeResponse != null && planMakeResponse.isSuccess) {
                        // 전송 성공
                        finish()
                        showToast("계획서를 성공적으로 저장했습니다")
                    } else {
                        // 전송 실패
                        Log.d(
                            "PlanDetailVisitActivity",
                            "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message"
                        )
                        showToast("계획서를 저장하는데 실패했습니다.")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("PlanDetailVisitActivity", "서버 오류 - $errorCode")
                    showToast("계획서를 저장하는데 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<PlanMakeResponse>, t: Throwable) {
                // 통신 실패
                Log.d("PlanDetailVisitActivity", "통신 실패 - ${t.message}")
                showToast("계획서를 저장하는데 실패했습니다.")
            }
        })
    }

    // 토스트 메시지 표시 함수 추가
    private fun showToast(message: String) {
        val toastBinding = LayoutToastBinding.inflate(LayoutInflater.from(this))
        toastBinding.toastMessage.text = message
        val toast = Toast(this)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}