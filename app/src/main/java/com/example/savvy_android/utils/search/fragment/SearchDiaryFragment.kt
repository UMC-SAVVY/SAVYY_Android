package com.example.savvy_android.utils.search.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentSearchDiaryBinding
import com.example.savvy_android.home.adapter.HomeAdapter
import com.example.savvy_android.home.adapter.HomeItemData
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.search.adapter.SearchRecordDiaryAdapter
import com.example.savvy_android.utils.search.data.RecordWordResponse
import com.example.savvy_android.utils.search.data.RecordWordResult
import com.example.savvy_android.utils.search.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchDiaryFragment : Fragment() {
    private lateinit var binding: FragmentSearchDiaryBinding
    private lateinit var recordAdapter: SearchRecordDiaryAdapter
    private var recordData = arrayListOf<RecordWordResult>()
    private lateinit var searchAdapter: HomeAdapter
    private var searchData = arrayListOf<HomeItemData>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchDiaryBinding.inflate(inflater, container, false)

        sharedPreferences =
            activity?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 닉네임 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.searchDiaryEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.searchDiaryEdit.length() != 0
                binding.searchDiaryBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.searchDiaryBtn)

                if (!isEnableState) {
                    binding.searchNoticeDiary.visibility = View.VISIBLE
                    binding.searchRecordDiaryRecycle.visibility = View.VISIBLE
                    binding.searchResultDiaryRecycle.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 검색 버튼 클릭 이벤트
        binding.searchDiaryBtn.setOnClickListener {
            // 안내 문구 안 보이도록 설정
            binding.searchNoticeDiary.visibility = View.GONE
            binding.searchRecordDiaryRecycle.visibility = View.GONE
            binding.searchResultDiaryRecycle.visibility = View.VISIBLE
        }

        // 검색 기록 삭제하기
        binding.searchWordClear.setOnClickListener {
            // 삭제 API를 넣어야함~~~
        }

        // 검색 기록에 대한 Data & Adapter
        recordAdapter = SearchRecordDiaryAdapter(recordData)
        binding.searchRecordDiaryRecycle.adapter = recordAdapter

        // 검색 결과에 대한 Data & Adapter
        searchAdapter = HomeAdapter(searchData)
        binding.searchResultDiaryRecycle.adapter = searchAdapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // 검색 기록 불러오기
        recordAdapter.clearList()
        searchDiaryRecordAPI()
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

    // 검색기록(제목/해시태그)
    private fun searchDiaryRecordAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val searchService = retrofit.create(SearchService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        searchService.recordWord(token = accessToken)
            .enqueue(object : Callback<RecordWordResponse> {
                override fun onResponse(
                    call: Call<RecordWordResponse>,
                    response: Response<RecordWordResponse>,
                ) {
                    if (response.isSuccessful) {
                        val recordWordResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (recordWordResponse?.isSuccess == true) {
                            Log.e("TEST", "불러와 단어!")
                            for (result in recordWordResponse.result) {
                                recordAdapter.addRecord(result)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            recordWordResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "SEARCH",
                                    detailType = "WORD",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "SEARCH",
                            "[SEARCH WORD] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<RecordWordResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[SEARCH WORD] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }
}