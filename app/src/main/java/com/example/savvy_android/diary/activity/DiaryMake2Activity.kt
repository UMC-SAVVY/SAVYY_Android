package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.plan.adapter.MakeDateAddAdapter
import com.example.savvy_android.databinding.ActivityDiaryStep2Binding
import com.example.savvy_android.databinding.ActivityPlanModifyBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.data.DiaryUploadRequest
import com.example.savvy_android.diary.data.DiaryUploadResponse
import com.example.savvy_android.diary.dialog.DiaryStopDialogFragment
import com.example.savvy_android.diary.service.DiaryUploadService
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.plan.activity.PlanModifyActivity
import com.example.savvy_android.plan.data.Checklist
import com.example.savvy_android.plan.data.PlanDetailResponse
import com.example.savvy_android.plan.data.PlanModifyRequest
import com.example.savvy_android.plan.data.PlanModifyResponse
import com.example.savvy_android.plan.data.Schedule
import com.example.savvy_android.plan.data.Timetable
import com.example.savvy_android.plan.service.PlanDetailService
import com.example.savvy_android.plan.service.PlanModifyService
import com.example.savvy_android.utils.memo.MemoModifyActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiaryMake2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep2Binding
    private lateinit var dateAddAdapter: MakeDateAddAdapter
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var sharedPreferences: SharedPreferences
    private var isDiary: Boolean = true
    private var planID: Int = 0
    private var newPlanID: Int = 0
    private var memoText: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작된 fragment 정보 받기
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        isDiary = intent.getBooleanExtra("isDiary", true)
        planID = intent.getIntExtra("planID", 0)
        memoText = intent.getStringExtra("memoText") ?: ""

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        val timetableList: MutableList<Timetable> = mutableListOf()
        dateAddAdapter = MakeDateAddAdapter(timetableList, supportFragmentManager, false)
        binding.recyclerviewTravelPlan.adapter = dateAddAdapter
        binding.recyclerviewTravelPlan.layoutManager = LinearLayoutManager(this)

//        // RecyclerView에 DateAddAdapter 설정
//        dateAddAdapter = MakeDateAddAdapter(mutableListOf(), supportFragmentManager, false)
//        binding.recyclerviewTravelPlan.adapter = dateAddAdapter
//        binding.recyclerviewTravelPlan.layoutManager = LinearLayoutManager(this)

//        val showDateAddItem = intent.getBooleanExtra("showDateAddItem", false)
//        if (showDateAddItem) {
//            // DateAdd 아이템 추가
////            dateAddAdapter.addItem("")
////
////            val newTimetable = Timetable("", mutableListOf(
////                Schedule(null, mutableListOf(
////                    Checklist(null, "", 0)), "", "", "")))
////            dateAddAdapter.addItem(newTimetable)
//        }


        //seek bar 애니메이션
        binding.seekBar.max = 4000

        valueAnimator = ValueAnimator.ofInt(1000, 2000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }

        valueAnimator.start()

        binding.diaryNextBtn.setOnClickListener {
            val title = binding.titleEdit.text.toString()
            val nickname = ""
            val update_at = ""
            val memoText = memoText

            val timetableList = dateAddAdapter.getDataList()

            val diaryUploadRequest = DiaryUploadRequest(null, memoText, nickname,
                timetableList, title, update_at)

            planUploadAPI(diaryUploadRequest)

            Log.d("PlanModifyRequest", "Request: $diaryUploadRequest")

        }

        // 뒤로가기 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // add_date_btn 클릭 시 새로운 날짜 추가
        binding.addDateBtn.setOnClickListener {
            val newTimetable = Timetable("", mutableListOf(Schedule(null, mutableListOf(Checklist(null, "", 0)), "", "", "")))
            dateAddAdapter.addItem(newTimetable)
            dateAddAdapter.isMake = true

        }


        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep2Tv.text.toString())

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 37, 49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan2, 70, 72, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep2Tv.text = spannableString

        planDetailAPI(planID, binding)

    }

    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiaryStopDialogFragment(isDiary)
        dialog.show(supportFragmentManager, "diaryStopDialog")
    }

    // 서버로 작성 데이터 전송하는 함수
    private fun planUploadAPI(diaryUploadRequest: DiaryUploadRequest) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val diaryUploadService = retrofit.create(DiaryUploadService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        diaryUploadService.planUpload(serverToken, diaryUploadRequest).enqueue(object :
            Callback<DiaryUploadResponse> {
            override fun onResponse(call: Call<DiaryUploadResponse>, response: Response<DiaryUploadResponse>) {
                if (response.isSuccessful) {
                    val diaryUploadResponse = response.body()
                    val isSuccess = diaryUploadResponse?.isSuccess
                    val code = diaryUploadResponse?.code
                    val message = diaryUploadResponse?.message
                    if (diaryUploadResponse != null && diaryUploadResponse.isSuccess) {
                        // 전송 성공
                        Log.d("DiaryMake2Activity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        newPlanID = diaryUploadResponse.result.planner_id

                        val intent = Intent(this@DiaryMake2Activity, DiaryMake3Activity::class.java)
                        intent.putExtra("isDiary", isDiary)
                        intent.putExtra("planID", newPlanID)
                        startActivity(intent)

                        Log.d("작성2 newPlanID", "newPlanID: $newPlanID")

                    } else {
                        // 전송 실패
                        Log.d("DiaryMake2Activity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    val errorMessage = response.message()
                    Log.d("DiaryMake2Activity", "서버 오류 - $errorCode  오류 메시지 = $errorMessage")
                }
            }

            override fun onFailure(call: Call<DiaryUploadResponse>, t: Throwable) {
                // 통신 실패
                Log.d("DiaryMake2Activity", "통신 실패 - ${t.message}")
            }
        })
    }

    private fun planDetailAPI(planId: Int, binding: ActivityDiaryStep2Binding) {
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
            override fun onResponse(call: Call<PlanDetailResponse>, response: Response<PlanDetailResponse>) {
                if (response.isSuccessful) {
                    val planDetailResponse = response.body()
                    val isSuccess = planDetailResponse?.isSuccess
                    val code = planDetailResponse?.code
                    val message = planDetailResponse?.message
                    if (planDetailResponse != null && planDetailResponse.isSuccess) {
                        val planDetailResult = planDetailResponse.result
                        // planDetailResult에 들어있는 데이터를 사용하여 작업
                        Log.d("PlanDetail(DiaryUpload)", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        binding.titleEdit.setText(planDetailResponse.result.title)

                        dateAddAdapter.addAllItems(planDetailResponse.result.timetable)

                        planID = planDetailResult.id

//                        memoText = planDetailResult.memo

                        // Memo 데이터를 MemoActivity로 전달
                        if (planDetailResult != null && planDetailResult.memo != null) {
                            memoText = planDetailResult.memo
                            Log.d("MemoModify", "memoText updated: $memoText")

                        } else {

                        }



                    } else {
                        Log.d("PlanDetail(DiaryUpload)", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                    }
                } else {
                    val errorCode = response.code()
                    Log.e("PlanDetail(DiaryUpload)", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<PlanDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.e("PlanDetail(DiaryUpload)", "통신 실패 - ${t.message}")
            }
        })
    }


}