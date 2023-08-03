package com.example.savvy_android.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.databinding.ItemHomeBinding

class HomeAdapter(
    private var homeList: ArrayList<HomeItemData>,
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class HomeViewHolder(binding: ItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var title = binding.itemHomeTitle
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
        holder.date.text = data.date
        holder.user.text = data.user
        holder.likeCount.text = data.likeCount.toString()
        holder.commentCount.text = data.commentCount.toString()
        if (data.img?.isEmpty() == false) {
            holder.img.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(data.img)
                .into(holder.img)
        } else {
            holder.img.visibility = View.GONE
        }
        if (data.tag?.isEmpty() == false) {
            holder.tag.visibility = View.VISIBLE
            holder.tag.text = data.tag
        } else {
            holder.tag.visibility = View.GONE
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = homeList.size

    // 데이터 추가
    fun addStorage(blockData: HomeItemData) {
        homeList.add(blockData)
        notifyItemInserted(homeList.size)
    }

    // 데이터 삭제
    private fun removePlan(position: Int) {
        if (position >= 0 && position < homeList.size) {
            homeList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearList(){
        homeList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }
}
