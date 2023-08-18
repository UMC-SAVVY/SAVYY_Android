package com.example.savvy_android.utils.alarm

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityAlarmBinding
import com.example.savvy_android.init.errorCodeList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private var alarmData = arrayListOf<AlarmResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 뒤로 가기 버튼 클릭 이벤트
        binding.alarmArrowIv.setOnClickListener {
            finish()
        }

        // alarm Data & Adapter
        alarmAdapter = AlarmAdapter(alarmData)
        binding.storageRecycle.adapter = alarmAdapter
    }

    override fun onResume() {
        super.onResume()
        alarmListAPI()
    }

    private fun alarmListAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val alarmService = retrofit.create(AlarmService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        alarmService.alarmList(token = accessToken)
            .enqueue(object : Callback<AlarmResponse> {
                override fun onResponse(
                    call: Call<AlarmResponse>,
                    response: Response<AlarmResponse>,
                ) {
                    if (response.isSuccessful) {
                        val recordWordResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (recordWordResponse?.isSuccess == true) {
                            Log.e("TEST"," ${ recordWordResponse.result }")
                            for (item in recordWordResponse.result) {
                                alarmAdapter.addAlarm(item)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            recordWordResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "ALARM",
                                    detailType = "LIST",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "ALARM",
                            "[ALARM LIST] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<AlarmResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("ALARM", "[ALARM LIST] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }
}