package com.example.savvy_android.plan.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityDiaryDetailBinding
import com.example.savvy_android.utils.memo.MemoActivity
import com.example.savvy_android.plan.adapter.DetailDateAdapter
import com.example.savvy_android.databinding.ActivityPlanDetialBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.activity.DiaryCommentActivity
import com.example.savvy_android.diary.dialog.DiaryDeleteDialogFragment
import com.example.savvy_android.diary.dialog.DiaryModifyDialogFragment
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.data.PlanDetailResponse
import com.example.savvy_android.plan.data.Timetable
import com.example.savvy_android.plan.data.remove.PlanRemoveResponse
import com.example.savvy_android.plan.dialog.PlanDeleteDialogFragment
import com.example.savvy_android.plan.service.PlanDetailService
import com.example.savvy_android.plan.service.PlanListService
import com.example.savvy_android.utils.BottomSheetDialogFragment
import com.example.savvy_android.utils.BottomSheetOtherDialogFragment
import com.example.savvy_android.utils.report.ReportActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlanDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanDetialBinding
    private lateinit var viewDateAdapter: DetailDateAdapter
    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의
    private var isMine: Boolean = true // 여행계획서가 본인것인지 판단
    private lateinit var nickname: String
    private var planID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityPlanDetialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)
        nickname = sharedPreferences.getString("USER_NICKNAME", null)!!


        planID = intent.getIntExtra("planID", 0)

        Log.d("test", "planID: $planID")

        val timetableList: MutableList<Timetable> = mutableListOf()
        viewDateAdapter = DetailDateAdapter(timetableList)
        binding.recyclerviewViewDate.adapter = viewDateAdapter
        binding.recyclerviewViewDate.layoutManager = LinearLayoutManager(this)

        // 뒤로 가기 버튼 클릭
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        planDetailAPI(planID, binding)
    }

    override fun onResume() {
        super.onResume()

        // 옵션 관련 (내가 작성한 다이어리)
        val bottomSheet = BottomSheetDialogFragment()
        bottomSheet.setButtonClickListener(object :
            BottomSheetDialogFragment.OnButtonClickListener {
            override fun onDialogEditClicked() {
                val intent = Intent(this@PlanDetailActivity, PlanModifyActivity::class.java)
                intent.putExtra("planID", planID)
                startActivity(intent)
                finish()
            }

            override fun onDialogDeleteClicked() {
                val dialog = PlanDeleteDialogFragment()

                // 다이얼로그 버튼 클릭 이벤트 설정
                dialog.setButtonClickListener(object :
                    PlanDeleteDialogFragment.OnButtonClickListener {
                    override fun onDialogPlanBtnOClicked() {
                        planRemoveAPI(planId = planID.toString())

                        // 리스트에서 해당 아이템 삭제하는 코드 추가
                        finish()
                    }

                    override fun onDialogPlanBtnXClicked() {
                    }
                })
                dialog.show(supportFragmentManager,"DiaryDeleteDialog")
            }
        })



        // 옵션 관련 (다른사람이 작성한 다이어리)
        val bottomSheetOther = BottomSheetOtherDialogFragment()
        bottomSheetOther.setButtonClickListener(object :
            BottomSheetOtherDialogFragment.OnButtonClickListener {
            override fun onDialogReportClicked() {
                val intent = Intent(this@PlanDetailActivity, ReportActivity::class.java)
                intent.putExtra("planID", planID)
                startActivity(intent)
            }
        })

        if(isMine){
            binding.memoCheckCardview.visibility = View.VISIBLE
            binding.optionBtn.setOnClickListener {
                bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")
            }
        }else{
            binding.memoCheckCardview.visibility = View.GONE
            binding.optionBtn.setOnClickListener {
                bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")
            }
        }
    }

    private fun planDetailAPI(planId: Int, binding: ActivityPlanDetialBinding) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val planDetailService = retrofit.create(PlanDetailService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!
        planDetailService.planDetail(serverToken, planId.toString()).enqueue(object : Callback<PlanDetailResponse> {
            override fun onResponse(call: Call<PlanDetailResponse>, response: Response<PlanDetailResponse>) {
                if (response.isSuccessful) {
                    val planDetailResponse = response.body()
                    val isSuccess = planDetailResponse?.isSuccess
                    val code = planDetailResponse?.code
                    val message = planDetailResponse?.message
                    if (planDetailResponse != null && planDetailResponse.isSuccess) {
                        val planDetailResult = planDetailResponse.result
                        // planDetailResult에 들어있는 데이터를 사용하여 작업
                        Log.d("PlanDetailActivity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        binding.travelPlanViewTitleTv.text = planDetailResult.title
                        binding.travelPlanViewUserTv.text = planDetailResult.nickname
                        binding.travelPlanViewUpdateTv.text = planDetailResult.updated_at

                        viewDateAdapter.addAllItems(planDetailResponse.result.timetable)

                        binding.memoCheckBtn.setOnClickListener {

                            if (planDetailResult != null && planDetailResult.memo != null) {
                                val memoText = planDetailResult.memo
                                val intent =
                                    Intent(this@PlanDetailActivity, MemoActivity::class.java)
                                intent.putExtra("memoText", memoText)
                                intent.putExtra("isMemoAdd", false)
                                startActivity(intent)

                            } else {
                                val intent =
                                    Intent(this@PlanDetailActivity, MemoActivity::class.java)
                                intent.putExtra("isMemoAdd", false)
                                startActivity(intent)
                            }
                        }

                        isMine = nickname == planDetailResult.nickname

                        planID = planDetailResult.id

                    } else {
                        Log.d("PlanDetailActivity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
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

    // 다이어리 삭제 API
    private fun planRemoveAPI(planId: String) {
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val planListService = retrofit.create(PlanListService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // Delete 요청
        planListService.planDelete(
            token = accessToken,
            plannerId = planId,
            plannerType = if (isMine) "0" else "1"
        )
            .enqueue(object : Callback<PlanRemoveResponse> {
                override fun onResponse(
                    call: Call<PlanRemoveResponse>,
                    response: Response<PlanRemoveResponse>,
                ) {
                    if (response.isSuccessful) {
                        val deleteResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (deleteResponse?.isSuccess == true) {
                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("성공적으로 여행계획서가 삭제되었습니다.")
                            finish()
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "PLAN",
                                    detailType = "DELETE",
                                    intentData = null
                                )
                            }
                            // 삭제 실패 시 토스트 메시지 표시
                            showToast("계획서 삭제를 실패하였습니다.")
                        }
                    } else {
                        Log.e(
                            "PLAN",
                            "[PLAN DELETE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                        // 삭제 실패 시 토스트 메시지 표시
                        showToast("계획서 삭제를 실패하였습니다.")
                    }
                }

                override fun onFailure(call: Call<PlanRemoveResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("PLAN", "[PLAN DELETE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                    // 삭제 실패 시 토스트 메시지 표시
                    showToast("계획서 삭제를 실패하였습니다.")
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