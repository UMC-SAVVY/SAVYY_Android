package com.example.savvy_android.plan.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.utils.memo.MemoModifyActivity
import com.example.savvy_android.plan.adapter.MakeDateAddAdapter
import com.example.savvy_android.databinding.ActivityPlanModifyBinding
import com.example.savvy_android.databinding.DialogPlanModifyBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.dialog.ModifySaveDialogFragment
import com.example.savvy_android.plan.adapter.DetailDateAdapter
import com.example.savvy_android.plan.data.Checklist
import com.example.savvy_android.plan.data.PlanDetailResponse
import com.example.savvy_android.plan.data.PlanModifyRequest
import com.example.savvy_android.plan.data.PlanModifyResponse
import com.example.savvy_android.plan.data.Schedule
import com.example.savvy_android.plan.data.Timetable
import com.example.savvy_android.plan.dialog.PlanDeleteDialogFragment
import com.example.savvy_android.plan.service.PlanDetailService
import com.example.savvy_android.plan.service.PlanModifyService
import com.example.savvy_android.utils.memo.MemoActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlanModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanModifyBinding
    private lateinit var dateAddAdapter: MakeDateAddAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var planID: Int = 0
    private var memoText: String = ""

    companion object {
        private const val MEMO_MODIFY_REQUEST_CODE = 123 // Use any unique integer value
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityPlanModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        planID = intent.getIntExtra("planID", 0)
        memoText = intent.getStringExtra("memoText") ?: ""

        val timetableList: MutableList<Timetable> = mutableListOf()
        dateAddAdapter = MakeDateAddAdapter(timetableList, supportFragmentManager, false)
        binding.recyclerviewDateAdd.adapter = dateAddAdapter
        binding.recyclerviewDateAdd.layoutManager = LinearLayoutManager(this)

        val modifySaveBinding = DialogPlanModifyBinding.inflate(layoutInflater)
        val modifySaveDialog = BottomSheetDialog(this)
        modifySaveDialog.setContentView(modifySaveBinding.root)


        //임시 title 입력 변화 이벤트 처리
        binding.titleEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트 변경 전 동작
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중 동작
            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0

                //한글자라도 입력하면 '완료'버튼의 색이 바뀜
                if (textLength > 0) {
                    binding.modifyCompletionBtn.setTextColor(Color.parseColor("#FF5487"))
                } else {
                    binding.titleEdit.setTextColor(Color.parseColor("#5F5F5F"))
                }
            }
        })

        // add_date_btn 클릭 시 새로운 날짜 추가
        binding.addDateBtn.setOnClickListener {
            val newTimetable = Timetable("", mutableListOf(Schedule(null, mutableListOf(Checklist(null, "", 0)), "", "", "")))
            dateAddAdapter.addItem(newTimetable)
            dateAddAdapter.isMake = true
        }

        // 뒤로가기 클릭 이벤트
        binding.icArrowLeft.setOnClickListener {
            finish()
        }

        // 수정 완료 버튼 클릭 이벤트
        binding.modifyCompletionBtn.setOnClickListener {
            val id = planID
            val titleText = binding.titleEdit.text.toString()
//            memoText = intent.getStringExtra("memoText") ?: ""
            val memoText = memoText
            Log.d("PlanModifyRequest", "memoText: $memoText") // Add this log statement

            val nickname = ""
            val currentTime = ""
            if (titleText.isNotEmpty()) {
                val dialog = ModifySaveDialogFragment()
                dialog.setButtonClickListener(object : ModifySaveDialogFragment.OnButtonClickListener {
                    override fun onDialogBtnOClicked() {
                        val timetableList = dateAddAdapter.getDataList()

                        val planModifyRequest = PlanModifyRequest(id, memoText, nickname,
                            timetableList, titleText, currentTime)

                        planModifyAPI(planModifyRequest)

                        Log.d("PlanModifyRequest", "Request: $planModifyRequest")
                    }

                    override fun onDialogBtnXClicked() {
                    }
                })
                dialog.show(supportFragmentManager, "modifySaveDialog")
            }
        }
        planDetailAPI(planID, binding)
    }

    // 서버로 작성 데이터 전송하는 함수
    private fun planModifyAPI(planModifyRequest: PlanModifyRequest) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val planModifyService = retrofit.create(PlanModifyService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        planModifyService.planModify(serverToken, planModifyRequest).enqueue(object :
            Callback<PlanModifyResponse> {
            override fun onResponse(call: Call<PlanModifyResponse>, response: Response<PlanModifyResponse>) {
                if (response.isSuccessful) {
                    val planModifyResponse = response.body()
                    val isSuccess = planModifyResponse?.isSuccess
                    val code = planModifyResponse?.code
                    val message = planModifyResponse?.message
                    if (planModifyResponse != null && planModifyResponse.isSuccess) {
                        // 전송 성공
                        Log.d("PlanModify", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        val intent = Intent(this@PlanModifyActivity, PlanDetailActivity::class.java)
                        intent.putExtra("planID", planID)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Add this line to clear previous instances of DetailActivity
                        startActivity(intent)

                        finish()
                    } else {
                        // 전송 실패
                        Log.d("PlanModify", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("PlanModifyActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<PlanModifyResponse>, t: Throwable) {
                // 통신 실패
                Log.d("PlanModifyActivity", "통신 실패 - ${t.message}")
            }
        })
    }

    private fun planDetailAPI(planId: Int, binding: ActivityPlanModifyBinding) {
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
                        Log.d("PlanDetail(Modify)", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        binding.titleEdit.setText(planDetailResponse.result.title)

                        dateAddAdapter.addAllItems(planDetailResponse.result.timetable)

                        planID = planDetailResult.id

                        binding.memoModifyBtn.setOnClickListener {

                            // Memo 데이터를 MemoActivity로 전달
                            if (planDetailResult != null && planDetailResult.memo != null) {
                                memoText = planDetailResult.memo
                                Log.d("MemoModify", "memoText updated: $memoText")
                                val intent = Intent(this@PlanModifyActivity, MemoModifyActivity::class.java)
                                intent.putExtra("memoText", memoText)
                               // startActivity(intent)
                                startActivityForResult(intent, MEMO_MODIFY_REQUEST_CODE)

                            } else {
                                val intent =
                                    Intent(this@PlanModifyActivity, MemoModifyActivity::class.java)
                               // startActivity(intent)
                                startActivityForResult(intent, MEMO_MODIFY_REQUEST_CODE)

                            }
                        }


                    } else {
                        Log.d("PlanDetail(Modify)", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                    }
                } else {
                    val errorCode = response.code()
                    Log.e("PlanDetail(Modify)", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<PlanDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.e("PlanDetail(Modify)", "통신 실패 - ${t.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MEMO_MODIFY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val updatedMemoText = data.getStringExtra("memoText")
            // Update the memoText variable with the new memoText value
            memoText = updatedMemoText ?: ""
            // You can also update any UI elements that display the memoText
        }
    }

}