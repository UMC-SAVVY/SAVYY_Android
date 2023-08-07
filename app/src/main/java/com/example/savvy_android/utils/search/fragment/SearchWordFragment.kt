package com.example.savvy_android.utils.search.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentSearchWordBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.home.adapter.HomeAdapter
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.LoadingDialogFragment
import com.example.savvy_android.utils.search.adapter.SearchRecordWordAdapter
import com.example.savvy_android.utils.search.data.DeleteRecordResponse
import com.example.savvy_android.utils.search.data.WordRecordResponse
import com.example.savvy_android.utils.search.data.WordRecordResult
import com.example.savvy_android.utils.search.data.WordSearchResponse
import com.example.savvy_android.utils.search.data.WordSearchResult
import com.example.savvy_android.utils.search.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchWordFragment : Fragment() {
    private lateinit var binding: FragmentSearchWordBinding
    private lateinit var recordAdapter: SearchRecordWordAdapter
    private var recordData = arrayListOf<WordRecordResult>()
    private lateinit var searchAdapter: HomeAdapter
    private var searchData = arrayListOf<WordSearchResult>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchWordBinding.inflate(inflater, container, false)

        // 닉네임 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.searchWordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.searchWordEdit.length() != 0
                binding.searchWordBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.searchWordBtn)

                if (!isEnableState) {   // editText에 아무것도 없을 때
                    binding.searchNoticeDiary.visibility = View.VISIBLE
                    binding.searchRecordWordRecycle.visibility = View.VISIBLE
                    binding.searchResultWordRecycle.visibility = View.GONE

                    // 검색 기록 불러오기
                    recordAdapter.clearList()
                    searchWordRecordAPI()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 검색 버튼 클릭 이벤트
        binding.searchWordBtn.setOnClickListener {
            // 안내 문구 안 보이도록 설정
            binding.searchNoticeDiary.visibility = View.GONE
            binding.searchRecordWordRecycle.visibility = View.GONE
            binding.searchResultWordRecycle.visibility = View.VISIBLE
            searchWordAPI(binding.searchWordEdit.text.toString())
        }

        // 검색 기록 삭제하기
        binding.searchWordClear.setOnClickListener {
            recordDeleteAllAPI()
        }
        // 검색 결과에 대한 Data & Adapter
        binding.searchResultWordRecycle.itemAnimator = null
        searchAdapter = HomeAdapter(requireContext(), searchData)
        binding.searchResultWordRecycle.adapter = searchAdapter

        // 검색 기록에 대한 Data & Adapter
        binding.searchRecordWordRecycle.itemAnimator = null
        recordAdapter = SearchRecordWordAdapter(
            context = requireContext(),
            wordRecordList = recordData,
            searchAdapter = searchAdapter,
            editText = binding.searchWordEdit,
            searchBtn = binding.searchWordBtn,
            recordRecycle = binding.searchRecordWordRecycle,
            resulutRecycle = binding.searchResultWordRecycle,
        )
        binding.searchRecordWordRecycle.adapter = recordAdapter

        // 검색 기록 불러오기
        searchWordRecordAPI()

        return binding.root
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

    // 검색기록 (제목/해시태그)
    private fun searchWordRecordAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val sharedPreferences: SharedPreferences =
            context?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val searchService = retrofit.create(SearchService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        searchService.recordWord(token = accessToken)
            .enqueue(object : Callback<WordRecordResponse> {
                override fun onResponse(
                    call: Call<WordRecordResponse>,
                    response: Response<WordRecordResponse>,
                ) {
                    if (response.isSuccessful) {
                        val recordWordResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (recordWordResponse?.isSuccess == true) {
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
                                    detailType = "WORD RECORD",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "SEARCH",
                            "[SEARCH WORD RECORD] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<WordRecordResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[SEARCH WORD RECORD] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    // 검색기록 전체 삭제 (제목/해시태그)
    private fun recordDeleteAllAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val sharedPreferences: SharedPreferences =
            context?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val searchService = retrofit.create(SearchService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        searchService.recordDeleteAll(token = accessToken, type = 0)
            .enqueue(object : Callback<DeleteRecordResponse> {
                override fun onResponse(
                    call: Call<DeleteRecordResponse>,
                    response: Response<DeleteRecordResponse>,
                ) {
                    if (response.isSuccessful) {
                        val recordWordResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (recordWordResponse?.isSuccess == true) {
                            recordAdapter.clearList()
                        } else {
                            // 응답 에러 코드 분류
                            recordWordResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "SEARCH",
                                    detailType = "DELETE RECORD ALL",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "SEARCH",
                            "[DELETE RECORD ALL] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<DeleteRecordResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[DELETE RECORD ALL] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    // 검색 (제목/해시태그)
    private fun searchWordAPI(word: String) {
        val dialog = LoadingDialogFragment()
        dialog.show(requireFragmentManager(), "LoadingDialog")
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val sharedPreferences: SharedPreferences =
            context?.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val searchService = retrofit.create(SearchService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        searchService.searchWord(token = accessToken, word = word)
            .enqueue(object : Callback<WordSearchResponse> {
                override fun onResponse(
                    call: Call<WordSearchResponse>,
                    response: Response<WordSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        val recordWordResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (recordWordResponse?.isSuccess == true) {
                            searchAdapter.clearList()
                            for (result in recordWordResponse.result) {
                                searchAdapter.addDiary(result)
                            }
                        } else if (recordWordResponse?.code == 2104) {
                            searchAdapter.clearList()
                            showToast("검색 결과가 없습니다.")
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
                    dialog.dismiss()
                }

                override fun onFailure(call: Call<WordSearchResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[SEARCH WORD] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                    dialog.dismiss()
                }
            })
    }

    // 토스트 메시지 표시 함수 추가
    private fun showToast(message: String) {
        val toastBinding = LayoutToastBinding.inflate(LayoutInflater.from(requireContext()))
        toastBinding.toastMessage.text = message
        val toast = Toast(requireContext())
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}