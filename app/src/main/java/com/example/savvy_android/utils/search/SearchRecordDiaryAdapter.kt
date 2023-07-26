package com.example.savvy_android.utils.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemSearchRecordWordBinding

class SearchRecordDiaryAdapter(private var searchList: ArrayList<SearchRecordDiaryItemData>) :
    RecyclerView.Adapter<SearchRecordDiaryAdapter.SearchTitleViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class SearchTitleViewHolder(binding: ItemSearchRecordWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var word = binding.searchNameTv
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchTitleViewHolder {
        val binding =
            ItemSearchRecordWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchTitleViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: SearchTitleViewHolder, position: Int) {
        holder.word.text  = searchList[position].name
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = searchList.size

    // 데이터 추가
    fun addPost(postData: SearchRecordDiaryItemData) {
        searchList.add(postData)
        notifyItemInserted(searchList.size)
    }
}
