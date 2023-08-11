package com.example.savvy_android.home.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentHomeBinding
import com.example.savvy_android.diary.activity.DiaryMake1Activity
import com.example.savvy_android.home.adapter.HomeAdapter
import com.example.savvy_android.home.service.HomeService
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.alarm.AlarmActivity
import com.example.savvy_android.utils.search.activity.SearchActivity
import com.example.savvy_android.home.data.HomeListResponse
import com.example.savvy_android.home.data.HomeListResult
import com.example.savvy_android.utils.LoadingDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeAdapter: HomeAdapter
    private var homeData = arrayListOf<HomeListResult>()
    private var isPaging = false // 페이징 요청 중인지 여부를 나타내는 플래그 변수
    private var isLastPage = false // 마지막 페이지까지 로딩되었는지 여부를 나타내는 플래그 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 알람 버튼 클릭 시 알람 페이지 연결
        binding.homeAlarm.setOnClickListener {
            val intent = Intent(context, AlarmActivity::class.java)
            startActivity(intent)
        }

        // 검색 버튼 클릭 시 검색 페이지 연결
        binding.homeSearch.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }

        // 홈화면 게시글 어뎁터
        homeAdapter = HomeAdapter(requireContext(), homeData)
        binding.homeRecycle.adapter = homeAdapter

        // 당겨서 새로고침 아이콘 색상
        binding.homeRefresh.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.main
            )
        )
        // 당겨서 새로 고침 할 때 진행할 작업
        binding.homeRefresh.setOnRefreshListener {
            // 리프레쉬 API
            binding.homeRefresh.isRefreshing = false
            homeListFirst()
        }

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.homeAddFbtn.setOnClickListener {
            val intent = Intent(context, DiaryMake1Activity::class.java)
            intent.putExtra("isDiary", false)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val hasAlarm = true
        if (hasAlarm)
            binding.homeAlarm.setImageResource(R.drawable.ic_alarm_o)
        else
            binding.homeAlarm.setImageResource(R.drawable.ic_alarm_x)

        // 생성될 때 목록 불러오기
        homeAdapter.clearList() // 리스트 정보 초기화
        homeListFirst()

        // 리사이클러뷰 스크롤의 마지막 감지 후 다음 페이징 API 호출
        val layoutManager = binding.homeRecycle.layoutManager as LinearLayoutManager
        binding.homeRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()


                if (!isPaging && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        val lastDiaryId = homeAdapter.getLastItemId()
                        homePagingAPI(lastDiaryId)
                    }
                }
            }
        })
    }

    // 홈 목록 조회 (처음)
    private fun homeListFirst() {
        isPaging = false
        var isFinish = false
        var isLoading = false
        val dialog = LoadingDialogFragment()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinish) {
                dialog.show(requireFragmentManager(), "LoadingDialog")
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
        val sharedPreferences: SharedPreferences =
            context?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val homeService = retrofit.create(HomeService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        homeService.homeListFirst(token = accessToken)
            .enqueue(object : Callback<HomeListResponse> {
                override fun onResponse(
                    call: Call<HomeListResponse>,
                    response: Response<HomeListResponse>,
                ) {
                    if (response.isSuccessful) {
                        val homeListResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (homeListResponse?.isSuccess == true) {
                            homeAdapter.clearList()
                            for (result in homeListResponse.result) {
                                homeAdapter.addDiary(result)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            homeListResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "HOME",
                                    detailType = "LIST",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "HOME",
                            "[HOME LIST] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                }

                override fun onFailure(call: Call<HomeListResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("HOME", "[HOME LIST] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                }
            })
    }

    // 홈 목록 조회 (페이징)
    private fun homePagingAPI(lastDiaryID: Int) {
        isPaging = true
        var isFinish = false
        var isLoading = false
        val dialog = LoadingDialogFragment()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinish) {
                dialog.show(requireFragmentManager(), "LoadingDialog")
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
        val sharedPreferences: SharedPreferences =
            context?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val homeService = retrofit.create(HomeService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        homeService.homePaging(token = accessToken, diaryId = lastDiaryID)
            .enqueue(object : Callback<HomeListResponse> {
                override fun onResponse(
                    call: Call<HomeListResponse>,
                    response: Response<HomeListResponse>,
                ) {
                    if (response.isSuccessful) {
                        val homeListResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (homeListResponse?.isSuccess == true) {
                            for (result in homeListResponse.result) {
                                homeAdapter.addDiary(result)
                            }
                        } else if (homeListResponse?.code == 2414) {
                            isLastPage = true
                        } else {
                            // 응답 에러 코드 분류
                            homeListResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "HOME",
                                    detailType = "LIST",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "HOME",
                            "[HOME LIST] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                }

                override fun onFailure(call: Call<HomeListResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("HOME", "[HOME LIST] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                    isPaging = false
                }
            })
    }
}