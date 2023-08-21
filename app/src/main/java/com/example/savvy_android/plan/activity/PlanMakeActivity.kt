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
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.utils.memo.MemoActivity
import com.example.savvy_android.plan.adapter.MakeDateAddAdapter
import com.example.savvy_android.databinding.ActivityPlanMakeBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.plan.data.Checklist
import com.example.savvy_android.plan.data.PlanMakeRequest
import com.example.savvy_android.plan.data.PlanMakeResponse
import com.example.savvy_android.plan.data.Schedule
import com.example.savvy_android.plan.data.Timetable
import com.example.savvy_android.plan.service.PlanMakeService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlanMakeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanMakeBinding
    private lateinit var dateAddAdapter: MakeDateAddAdapter
    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의

    companion object {
        const val MEMO_REQUEST_CODE = 1 // 임의의 상수 값으로 설정합니다.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityPlanMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

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
                    binding.makeCompletionBtn.setTextColor(Color.parseColor("#FF5487"))
                } else {
                    binding.titleEdit.setTextColor(Color.parseColor("#5F5F5F"))
                }
            }
        })

        // add_date_btn 클릭 시 새로운 날짜 추가
        binding.addDateBtn.setOnClickListener {
            val newTimetable = Timetable("", mutableListOf(Schedule(null,mutableListOf(Checklist(null,"", 0)), "", "", "")))
            dateAddAdapter.addItem(newTimetable)
            dateAddAdapter.isMake = true
        }

        // RecyclerView에 DateAddAdapter 설정
        dateAddAdapter = MakeDateAddAdapter(mutableListOf(
            Timetable("", mutableListOf(
                Schedule(null, mutableListOf(Checklist(null, "", 0)
        ), "", "", "")))), supportFragmentManager, true)
        binding.recyclerviewDateAdd.adapter = dateAddAdapter
        binding.recyclerviewDateAdd.layoutManager = LinearLayoutManager(this)

        // 뒤로가기 클릭 이벤트
        binding.icArrowLeft.setOnClickListener {
            finish()
        }

        // 만들기 완료 버튼 클릭 이벤트
        binding.makeCompletionBtn.setOnClickListener {
            val titleText = binding.titleEdit.text.toString()
            val memoText = intent.getStringExtra("memoText") ?: ""
            val nickname = ""
            val currentTime = ""
            if (titleText.isNotEmpty()) {
                val timetableList = dateAddAdapter.getDataList()
                val planMakeRequest = PlanMakeRequest(null, memoText, nickname,
                    timetableList, titleText, currentTime)

                planMakeAPI(planMakeRequest)
                finish()
            }else{
                showToast("제목을 입력해주세요")
            }
        }

        // 메모 추가하기 버튼 클릭 이벤트
        binding.memoAddBtn.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            intent.putExtra("isMemoAdd", true)
            startActivityForResult(intent, MEMO_REQUEST_CODE)
        }

        // 뒤로가기 버튼 클릭 이벤트
        binding.icArrowLeft.setOnClickListener {
            finish()
        }

    }

    // 서버로 작성 데이터 전송하는 함수
    private fun planMakeAPI(planMakeRequest: PlanMakeRequest) {
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
            override fun onResponse(call: Call<PlanMakeResponse>, response: Response<PlanMakeResponse>) {
                if (response.isSuccessful) {
                    val planMakeResponse = response.body()
                    val isSuccess = planMakeResponse?.isSuccess
                    val code = planMakeResponse?.code
                    val message = planMakeResponse?.message
                    if (planMakeResponse != null && planMakeResponse.isSuccess) {
                        // 전송 성공
                        Log.d("PlanMakeActivity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")
                        finish()
                    } else {
                        // 전송 실패
                        Log.d("PlanMakeActivity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("PlanMakeActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<PlanMakeResponse>, t: Throwable) {
                // 통신 실패
                Log.d("PlanMakeActivity", "통신 실패 - ${t.message}")
            }
        })
    }
    // onActivityResult 함수 오버라이드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEMO_REQUEST_CODE && resultCode == RESULT_OK) {
            // MemoActivity로부터 전달받은 데이터를 사용
            val memoText = data?.getStringExtra("memoText") ?: ""

            intent.putExtra("memoText", memoText)
        }
    }

    // 토스트 메시지
    private fun showToast(message: String) {
        val toastBinding =
            LayoutToastBinding.inflate(LayoutInflater.from(this@PlanMakeActivity))
        toastBinding.toastMessage.text = message
        val toast = Toast(this@PlanMakeActivity)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

}