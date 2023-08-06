package com.example.savvy_android.home.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemHomeBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.utils.search.data.WordSearchResponse
import com.example.savvy_android.utils.search.data.WordSearchResult
import com.example.savvy_android.utils.search.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeAdapter(
    private val context: Context,
    private var homeList: ArrayList<WordSearchResult>,
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class HomeViewHolder(binding: ItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var title = binding.itemHomeTitle
        var imgLayout = binding.itemHomeImgLayout
        var img = binding.itemHomeViewImg
        var date = binding.itemHomeDate
        var tag = binding.itemHomeTag
        var user = binding.itemHomeUser
        var likeCount = binding.itemHomeLikeTv
        var commentCount = binding.itemHomeCommentTv
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeViewHolder {
        val binding =
            ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val data = homeList[position]
        holder.title.text = data.title
        holder.date.text = data.updated_at
        holder.user.text = data.nickname
        holder.likeCount.text = data.likes_count.toString()
        holder.commentCount.text = data.comments_count.toString()
        if (data.thumbnail?.isNotEmpty() == true) {
            holder.imgLayout.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(data.thumbnail)
                .into(holder.img)
        } else {
            Log.e("TEST","작동하냐 ㅅㅂ: ${data.id}")
            holder.imgLayout.visibility = View.GONE
        }
        if (data.hashtag?.isEmpty() == false) {
            holder.tag.visibility = View.VISIBLE
            var tempHashtag = ""
            for (item in data.hashtag!!) {
                tempHashtag += "# ${item.tag} "
            }
            holder.tag.text = tempHashtag
        } else {
            holder.tag.visibility = View.GONE
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = homeList.size

    // 데이터 추가
    fun addDiary(blockData: WordSearchResult) {
        homeList.add(blockData)
        notifyItemInserted(homeList.size)
    }

    // 데이터 삭제
    private fun removeDiary(position: Int) {
        if (position >= 0 && position < homeList.size) {
            homeList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearList() {
        homeList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }
}
