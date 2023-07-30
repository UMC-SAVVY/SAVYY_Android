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
import com.example.savvy_android.diary.data.list.DiaryListResponse
import com.example.savvy_android.diary.data.list.DiaryListResult
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.errorCodeList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageDiaryFragment : Fragment() {
    private lateinit var binding: FragmentMypageDiaryBinding
    private lateinit var diaryListAdapter: DiaryListAdapter
    private var diaryListData = arrayListOf<DiaryListResult>()
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
                binding.diaryRecycle,
                diaryListData,
                requireActivity().supportFragmentManager,
                false
            )
        binding.diaryRecycle.adapter = diaryListAdapter


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        diaryListAdapter.clearList() // 리스트 정보 초기화

        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val diaryListService = retrofit.create(DiaryService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        diaryListService.diaryListMine(token = accessToken)
            .enqueue(object : Callback<DiaryListResponse> {
                override fun onResponse(
                    call: Call<DiaryListResponse>,
                    response: Response<DiaryListResponse>,
                ) {
                    if (response.isSuccessful) {
                        val planResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (planResponse?.isSuccess == true && planResponse.code == 1000) {
                            for (result in planResponse.result) {
                                diaryListAdapter.addPlan(
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
                        } else {
                            // 응답 에러 코드 분류
                            planResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "MINE",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY MINE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<DiaryListResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("DIARY", "[DIARY MINE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }
}