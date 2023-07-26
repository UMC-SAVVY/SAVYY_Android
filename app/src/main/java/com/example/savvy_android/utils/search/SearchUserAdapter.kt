package com.example.savvy_android.utils.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.databinding.ItemSearchUserBinding

class SearchUserAdapter(private var searchList: ArrayList<SearchUserItemData>) :
    RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class SearchUserViewHolder(binding: ItemSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var name = binding.searchName
        var img = binding.searchProfileImg
        var likeCount = binding.searchLikeCount
        var planCount = binding.searchPlanCount
        var diaryCount = binding.searchDiaryCount
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
        val data = searchList[position]
        holder.name.text = data.name
        holder.likeCount.text = data.likeCount.toString()
        holder.planCount.text = data.planCount.toString()
        holder.diaryCount.text = data.diaryCount.toString()
        Glide.with(holder.itemView)
            .load(data.profileImg)
            .into(holder.img)
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = searchList.size

    // 데이터 추가
    fun addPost(postData: SearchUserItemData) {
        searchList.add(postData)
        notifyItemInserted(searchList.size)
    }
}
