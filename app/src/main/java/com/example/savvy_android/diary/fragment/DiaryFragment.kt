package com.example.savvy_android.diary.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.savvy_android.R
import com.example.savvy_android.diary.activity.DiaryMake1Activity
import com.example.savvy_android.diary.adapter.DiaryListAdapter
import com.example.savvy_android.databinding.FragmentDiaryBinding
import com.example.savvy_android.diary.DiaryItemTouchCallback
import com.example.savvy_android.diary.data.list.DiaryListResponse
import com.example.savvy_android.diary.data.list.DiaryListResult
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.alarm.AlarmActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiaryFragment : Fragment() {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var diaryListAdapter: DiaryListAdapter
    private var diaryListData = arrayListOf<DiaryListResult>()
    private val diaryTouchSimpleCallback = DiaryItemTouchCallback()
    private val itemTouchHelper = ItemTouchHelper(diaryTouchSimpleCallback)
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        sharedPreferences =
            activity?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 알람 버튼 클릭시 알람 페이지 연결
        binding.diaryAlarm.setOnClickListener {
            val intent = Intent(context, AlarmActivity::class.java)
            startActivity(intent)
        }

        // 검색 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.diarySearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.diarySearchEdit.length() != 0
                binding.diarySearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.diarySearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Diary Data & Adapter
        diaryListAdapter =
            DiaryListAdapter(
                requireContext(),
                binding.diaryRecycle,
                diaryListData,
                requireActivity().supportFragmentManager,
                true
            )
        binding.diaryRecycle.adapter = diaryListAdapter


        // itemTouchHelper와 recyclerview 연결
        itemTouchHelper.attachToRecyclerView(binding.diaryRecycle)

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.diaryAddFbtn.setOnClickListener {
            val intent = Intent(context, DiaryMake1Activity::class.java)
            intent.putExtra("isDiary",true)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // 알람 존재 여부에 따른 알람 버튼 형태
        val hasAlarm = true
        if (hasAlarm)
            binding.diaryAlarm.setImageResource(R.drawable.ic_alarm_o)
        else
            binding.diaryAlarm.setImageResource(R.drawable.ic_alarm_x)

        // 검색 기능
        binding.diarySearchBtn.setOnClickListener {
            Log.e("TEST", "검색 버튼 눌림")
            searchDiaryList(binding.diarySearchEdit.text.toString())
        }

        // 목록 (나의 다이어리) 다시 불러오기
        diaryListAdapter.clearList() // 리스트 정보 초기화
        diaryListAPI()
    }

    // 클릭 가능 여부에 따른 button 배경 변경 함수
    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val context: Context = requireContext()
        val buttonColor = if (able) {
            ContextCompat.getColor(context, R.color.main)
        } else {
            ContextCompat.getColor(context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }

    // 목록 검색 API
    private fun searchDiaryList(searchText: String) {
        Log.e("TEST", "$searchText")
        // 검색 단어를 포함하는지 확인
        // 검색 API
    }

    // 다이어리 목록(나의 다이어리) API
    private fun diaryListAPI(){
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
                                    type = "PLAN",
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