package com.example.savvy_android.utils.search.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemSearchUserBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.search.activity.SearchDetailUserActivity
import com.example.savvy_android.utils.search.data.DeleteRecordResponse
import com.example.savvy_android.utils.search.data.UserResult
import com.example.savvy_android.utils.search.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchUserAdapter(
    private val context: Context,
    private var searchList: ArrayList<UserResult>,
    private val isRecord: Boolean,
) :
    RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class SearchUserViewHolder(binding: ItemSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var name = binding.searchName
        var img = binding.searchProfileImg
        var likeCount = binding.searchLikeCount
        var planCount = binding.searchPlanCount
        var diaryCount = binding.searchDiaryCount
        var deleteBtn = binding.searchDelete
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchUserViewHolder {
        val binding =
            ItemSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchUserViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        if (isRecord) {
            holder.deleteBtn.visibility = View.VISIBLE
            holder.deleteBtn.isEnabled = true
        } else {
            holder.deleteBtn.visibility = View.GONE
            holder.deleteBtn.isEnabled = false
        }
        val data = searchList[position]
        holder.name.text = data.nickname
        holder.likeCount.text = data.likes.toString()
        holder.planCount.text = data.amount_planner.toString()
        holder.diaryCount.text = data.amount_diary.toString()
        if (data.pic_url != null) {
            Glide.with(holder.itemView)
                .load(data.pic_url)
                .into(holder.img)
        }
        holder.deleteBtn.setOnClickListener {
            recordDeleteAPI(word = holder.name.text.toString(), position = position)
        }

        holder.itemView.setOnClickListener {
            val mIntent = Intent(holder.itemView.context, SearchDetailUserActivity::class.java)
            mIntent.putExtra("userId", data.id)
            holder.itemView.context.startActivity(mIntent)
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = searchList.size

    // 데이터 추가
    fun addUser(postData: UserResult) {
        searchList.add(postData)
        notifyItemInserted(searchList.size)
    }

    // 데이터 삭제
    private fun removeRecord(position: Int) {
        if (position >= 0 && position < searchList.size) {
            searchList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearList() {
        searchList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }

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

        searchService.recordDelete(token = accessToken, word = word, type = 1)
            .enqueue(object : Callback<DeleteRecordResponse> {
                override fun onResponse(
                    call: Call<DeleteRecordResponse>,
                    response: Response<DeleteRecordResponse>,
                ) {
                    Log.e("TEST", "$word")
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
}
