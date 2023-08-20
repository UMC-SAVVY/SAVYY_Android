package com.example.savvy_android.myPage.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentMypageDiaryBinding
import com.example.savvy_android.diary.adapter.DiaryListAdapter
import com.example.savvy_android.diary.data.list.DiaryListResult
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.myPage.data.UserDiaryResponse
import com.example.savvy_android.myPage.service.MyPageService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageDiaryFragment(
    private val isSearching: Boolean,
    private val userId: Int,
) : Fragment() {
    private lateinit var binding: FragmentMypageDiaryBinding
    private lateinit var diaryListAdapter: DiaryListAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMypageDiaryBinding.inflate(inflater, container, false)

        sharedPreferences =
            activity?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // Plan Data & Adapter
        diaryListAdapter =
            DiaryListAdapter(
                requireContext(),
                binding.diaryRecycle,
                requireActivity().supportFragmentManager,
                false
            )
        binding.diaryRecycle.adapter = diaryListAdapter


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        diaryListAdapter.clearList() // 리스트 정보 초기화

        if (isSearching)
            otherDiaryAPI(userId)
        else
            myDiaryAPI()
    }

    private fun myDiaryAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val myPageService = retrofit.create(MyPageService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        myPageService.myPageDiary(token = accessToken)
            .enqueue(object : Callback<UserDiaryResponse> {
                override fun onResponse(
                    call: Call<UserDiaryResponse>,
                    response: Response<UserDiaryResponse>,
                ) {
                    if (response.isSuccessful) {
                        val myPageResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (myPageResponse?.isSuccess == true) {
                            if (myPageResponse.result.amount_diary > 0) {
                                val tempList = arrayListOf<DiaryListResult>()
                                for (result in myPageResponse.result.diary) {
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
                                diaryListAdapter.submitList(tempList)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            myPageResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "MYPAGE DIARY",
                                    detailType = "MINE DIARY",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "MYPAGE DIARY",
                            "[MYPAGE DIARY MINE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserDiaryResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e(
                        "MYPAGE DIARY",
                        "[MYPAGE DIARY MINE] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                    )
                }
            })
    }

    private fun otherDiaryAPI(userId: Int) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val myPageService = retrofit.create(MyPageService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        myPageService.otherPageDiary(token = accessToken, userId = userId)
            .enqueue(object : Callback<UserDiaryResponse> {
                override fun onResponse(
                    call: Call<UserDiaryResponse>,
                    response: Response<UserDiaryResponse>,
                ) {
                    if (response.isSuccessful) {
                        val userPageResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (userPageResponse?.isSuccess == true) {
                            if (userPageResponse.result.amount_diary > 0) {
                                val tempList = arrayListOf<DiaryListResult>()
                                for (result in userPageResponse.result.diary) {
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
                                diaryListAdapter.submitList(tempList)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            userPageResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "MINE DIARY",
                                    detailType = "OTHER",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "MYPAGE DIARY",
                            "[MYPAGE DIARY OTHER] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserDiaryResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("MYPAGE", "[MYPAGE DIARY OTHER] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }
}