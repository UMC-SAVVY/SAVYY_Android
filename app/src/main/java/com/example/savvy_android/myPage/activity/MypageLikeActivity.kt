package com.example.savvy_android.myPage.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityMypageLikeBinding
import com.example.savvy_android.diary.data.list.DiaryListResponse
import com.example.savvy_android.diary.data.list.DiaryListResult
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.myPage.DiaryLikeItemTouchCallback
//import com.example.savvy_android.myPage.DiaryLikeItemTouchCallback
import com.example.savvy_android.myPage.adapter.DiaryLikeListAdapter
import com.example.savvy_android.myPage.service.MyPageService
import com.example.savvy_android.utils.LoadingDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageLikeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageLikeBinding
    private lateinit var diaryListAdapter: DiaryLikeListAdapter
    private val diaryTouchSimpleCallback = DiaryLikeItemTouchCallback()
    private val itemTouchHelper = ItemTouchHelper(diaryTouchSimpleCallback)
    private lateinit var sharedPreferences: SharedPreferences
    private var isPause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityMypageLikeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 뒤로 가기 버튼 클릭 이벤트
        binding.likeArrowIv.setOnClickListener {
            finish()
        }

        // Diary Data & Adapter
        diaryListAdapter =
            DiaryLikeListAdapter(
                this,
                binding.diaryLikeRecycle,
                supportFragmentManager,
                true
            )
        binding.diaryLikeRecycle.adapter = diaryListAdapter


        // itemTouchHelper와 recyclerview 연결
        itemTouchHelper.attachToRecyclerView(binding.diaryLikeRecycle)
    }

    override fun onResume() {
        super.onResume()
        isPause = false

        diaryListAPI()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    // 다이어리 목록(좋아요) API
    private fun diaryListAPI() {
        var isFinish = false
        var isLoading = false
        val dialog = LoadingDialogFragment()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinish) {
                dialog.show(supportFragmentManager, "LoadingDialog")
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
        val diaryListService = retrofit.create(MyPageService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        diaryListService.likeDiary(token = accessToken)
            .enqueue(object : Callback<DiaryListResponse> {
                override fun onResponse(
                    call: Call<DiaryListResponse>,
                    response: Response<DiaryListResponse>,
                ) {
                    if (response.isSuccessful) {
                        val planResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (planResponse?.isSuccess == true && planResponse.code == 1000) {
                            val tempList = arrayListOf<DiaryListResult>()
                            for (result in planResponse.result) {
                                tempList.add(
                                    DiaryListResult(
                                        id = result.id,
                                        title = result.title,
                                        updated_at = result.updated_at,
                                        likes_count = result.likes_count,
                                        comments_count = result.comments_count,
                                        thumbnail = result.thumbnail,
                                        img_count = result.img_count,
                                        is_public = result.is_public,
                                    )
                                )
                            }
                            Log.e("TEST","$planResponse.result")
                            diaryListAdapter.submitList(tempList)
                        } else {
                            // 응답 에러 코드 분류
                            planResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "MYPAGE",
                                    detailType = "LIKE",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "MYPAGE",
                            "[MYPAGE LIKE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                }

                override fun onFailure(call: Call<DiaryListResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("MYPAGE", "[MYPAGE LIKE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

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