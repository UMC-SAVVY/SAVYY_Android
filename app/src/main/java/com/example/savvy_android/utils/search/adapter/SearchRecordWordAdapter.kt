package com.example.savvy_android.utils.search.adapter

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemSearchRecordWordBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.home.adapter.HomeAdapter
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.search.data.DeleteRecordResponse
import com.example.savvy_android.utils.search.data.WordRecordResult
import com.example.savvy_android.utils.search.data.WordSearchResponse
import com.example.savvy_android.utils.search.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchRecordWordAdapter(
    private val context: Context,
    private var wordRecordList: ArrayList<WordRecordResult>,
    private val searchAdapter: HomeAdapter,
    private val editText: EditText,
    private val searchBtn: AppCompatButton,
    private val recordRecycle: RecyclerView,
    private val resulutRecycle: RecyclerView,
) :
    RecyclerView.Adapter<SearchRecordWordAdapter.SearchWordViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class SearchWordViewHolder(binding: ItemSearchRecordWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var word = binding.searchNameTv
        val deleteBtn = binding.searchDelete
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchWordViewHolder {
        val binding =
            ItemSearchRecordWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchWordViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: SearchWordViewHolder, position: Int) {
        holder.word.text = wordRecordList[position].search_word
        holder.word.setOnClickListener {
            searchRecordWordAPI(word = holder.word.text.toString())
        }
        holder.deleteBtn.setOnClickListener {
            recordDeleteAPI(word = holder.word.text.toString(), position = position)
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = wordRecordList.size

    // 데이터 추가
    fun addRecord(postData: WordRecordResult) {
        wordRecordList.add(postData)
        notifyItemInserted(wordRecordList.size)
    }

    // 데이터 삭제
    private fun removeRecord(position: Int) {
        if (position >= 0 && position < wordRecordList.size) {
            wordRecordList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearList() {
        wordRecordList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }

    // 클릭 가능 여부에 따른 button 배경 변경 함수
    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val buttonColor = if (able) {
            ContextCompat.getColor(context, R.color.main)
        } else {
            ContextCompat.getColor(context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }

    // 검색 기록 삭제
    private fun recordDeleteAPI(word: String, position: Int) {
        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val searchService = retrofit.create(SearchService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        searchService.recordDelete(token = accessToken, word = word, type = 0)
            .enqueue(object : Callback<DeleteRecordResponse> {
                override fun onResponse(
                    call: Call<DeleteRecordResponse>,
                    response: Response<DeleteRecordResponse>,
                ) {
                    if (response.isSuccessful) {
                        val recordDeleteResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (recordDeleteResponse?.isSuccess == true) {
                            removeRecord(position)
                        } else {
                            // 응답 에러 코드 분류
                            recordDeleteResponse?.let {
                                context.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "SEARCH",
                                    detailType = "DELETE",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "SEARCH",
                            "[SEARCH DELETE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<DeleteRecordResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[SEARCH DELETE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    // 검색 (제목/해시태그)
    private fun searchRecordWordAPI(word: String) {
        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
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
                        editText.setText(word)
                        btnStateBackground(true, searchBtn)
                        recordRecycle.visibility = View.GONE
                        resulutRecycle.visibility = View.VISIBLE
                        searchAdapter.clearList()
                        if (recordWordResponse?.isSuccess == true) {
                            for (result in recordWordResponse.result) {
                                searchAdapter.addDiary(result)
                            }
                        } else if (recordWordResponse?.code == 2104) {
                            showToast("검색 결과가 없습니다.")
                        } else {
                            // 응답 에러 코드 분류
                            recordWordResponse?.let {
                                context.errorCodeList(
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

                override fun onFailure(call: Call<WordSearchResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("SEARCH", "[SEARCH WORD] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    // 토스트 메시지 표시 함수 추가
    private fun showToast(message: String) {
        val toastBinding = LayoutToastBinding.inflate(LayoutInflater.from(context))
        toastBinding.toastMessage.text = message
        val toast = Toast(context)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}
