package com.example.savvy_android.utils.search.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.savvy_android.databinding.FragmentSearchUserBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.LoadingDialogFragment
import com.example.savvy_android.utils.search.adapter.SearchUserAdapter
import com.example.savvy_android.utils.search.data.DeleteRecordResponse
import com.example.savvy_android.utils.search.data.UserResponse
import com.example.savvy_android.utils.search.data.UserResult
import com.example.savvy_android.utils.search.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchUserFragment : Fragment() {
    private lateinit var binding: FragmentSearchUserBinding
    private lateinit var recordAdapter: SearchUserAdapter
    private var recordData = arrayListOf<UserResult>()
    private lateinit var searchAdapter: SearchUserAdapter
    private var searchData = arrayListOf<UserResult>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchUserBinding.inflate(inflater, container, false)

        // 닉네임 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.searchUserEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.searchUserEdit.length() != 0
                binding.searchUserBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.searchUserBtn)

                if (!isEnableState) { // editText에 아무것도 없을 때
                    binding.searchNoticeUser.visibility = View.VISIBLE
                    binding.searchRecordUserRecycle.visibility = View.VISIBLE
                    binding.searchResultUserRecycle.visibility = View.GONE

                    // 검색 기록 불러오기
                    recordAdapter.clearList()
                    searchUserRecordAPI()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 검색 버튼 클릭 이벤트
        binding.searchUserBtn.setOnClickListener {
            // 안내 문구 안 보이도록 설정
            binding.searchNoticeUser.visibility = View.GONE
            binding.searchRecordUserRecycle.visibility = View.GONE
            binding.searchResultUserRecycle.visibility = View.VISIBLE
            searchUserAPI(binding.searchUserEdit.text.toString())
        }

        // 검색 기록 삭제하기
        binding.searchUserClear.setOnClickListener {
            recordDeleteAllAPI()
        }

        // 검색 기록에 대한 Data & Adapter
        binding.searchRecordUserRecycle.itemAnimator = null
        recordAdapter = SearchUserAdapter(requireContext(), recordData, isRecord = true)
        binding.searchRecordUserRecycle.adapter = recordAdapter

        // 검색 기록 불러오기
        searchUserRecordAPI()

        // 검색 결과에 대한 Data & Adapter
        binding.searchResultUserRecycle.itemAnimator = null
        searchAdapter = SearchUserAdapter(requireContext(), searchData, isRecord = false)
        binding.searchResultUserRecycle.adapter = searchAdapter

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

    // 검색기록 (사용자)
    private fun searchUserRecordAPI() {
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

        searchService.recordUser(token = accessToken)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>,
                ) {
                    if (response.isSuccessful) {
                        val recordUserResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (recordUserResponse?.isSuccess == true) {
                            for (result in recordUserResponse.result) {
                                recordAdapter.addUser(result)
                            }
                        } else {
                            // 응답 에러 코드 분류
                            recordUserResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "SEARCH",
                                    detailType = "USER RECORD",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "SEARCH",
                            "[SEARCH USER RECORD] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[SEARCH USER RECORD] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    // 검색기록 전체 삭제 (사용자)
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

        searchService.recordDeleteAll(token = accessToken, type = 1)
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

    // 검색 (사용자)
    private fun searchUserAPI(word: String) {
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
        val searchService = retrofit.create(SearchService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        searchService.searchUser(token = accessToken, word = word)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>,
                ) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (userResponse?.isSuccess == true) {
                            searchAdapter.clearList()
                            for (result in userResponse.result) {
                                searchAdapter.addUser(result)
                            }
                        } else if (userResponse?.code == 2104) {
                            searchAdapter.clearList()
                            showToast("검색 결과가 없습니다.")
                        } else {
                            // 응답 에러 코드 분류
                            userResponse?.let {
                                context?.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "SEARCH",
                                    detailType = "USER",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "SEARCH",
                            "[SEARCH USER] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[SEARCH USER] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
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