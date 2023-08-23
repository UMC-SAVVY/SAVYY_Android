package com.example.savvy_android.myPage.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityMypageBlockBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.myPage.adapter.MypageBlockAdapter
import com.example.savvy_android.myPage.data.BlockReleaseResponse
import com.example.savvy_android.myPage.data.MyPageBlockResponse
import com.example.savvy_android.myPage.data.MyPageBlockResult
import com.example.savvy_android.myPage.service.BlockService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageBlockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBlockBinding
    private lateinit var blockAdapter: MypageBlockAdapter
    private var blockData = arrayListOf<MyPageBlockResult>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityMypageBlockBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 뒤로 가기 버튼 클릭 이벤트
        binding.storagePlaceArrowIv.setOnClickListener {
            finish()
        }
        // SearchNewPlace Data & Adapter
        blockAdapter = MypageBlockAdapter(this, blockData)
        binding.storageRecycle.adapter = blockAdapter
    }

    override fun onResume() {
        super.onResume()

        // 목록 불러오기
        blockAdapter.clearList() // 리스트 정보 초기화
        blockListAPI()
    }

    // 차단 목록 API
    private fun blockListAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val blockService = retrofit.create(BlockService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        blockService.blockList(token = accessToken)
            .enqueue(object : Callback<MyPageBlockResponse> {
                override fun onResponse(
                    call: Call<MyPageBlockResponse>,
                    response: Response<MyPageBlockResponse>,
                ) {
                    if (response.isSuccessful) {
                        val blockResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (blockResponse?.isSuccess == true && blockResponse.code == 1000) {
                            for (result in blockResponse.result) {
                                blockAdapter.addBlock(
                                    MyPageBlockResult(
                                        blocked_user = result.blocked_user,
                                        nickname = result.nickname
                                    )
                                )
                            }
                        } else {
                            // 응답 에러 코드 분류
                            blockResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "BLOCK",
                                    detailType = "LIST",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "BLOCK",
                            "[BLOCK LIST] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<MyPageBlockResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("BLOCK", "[BLOCK LIST] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }
}