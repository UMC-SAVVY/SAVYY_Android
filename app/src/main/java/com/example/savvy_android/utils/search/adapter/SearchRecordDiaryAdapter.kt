package com.example.savvy_android.utils.search.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemSearchRecordWordBinding
import com.example.savvy_android.utils.search.data.RecordWordResult

class SearchRecordDiaryAdapter(
    private var wordRecordList: ArrayList<RecordWordResult>,
) :
    RecyclerView.Adapter<SearchRecordDiaryAdapter.SearchWordViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class SearchWordViewHolder(binding: ItemSearchRecordWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var word = binding.searchNameTv
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
        Log.e("TEST", "Adapter: ${wordRecordList[position].search_word}")
        Log.e("TEST", "text: ${holder.word.text}")
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = wordRecordList.size

    // 데이터 추가
    fun addRecord(postData: RecordWordResult) {
        wordRecordList.add(postData)
        notifyItemInserted(wordRecordList.size)
    }

    // 데이터 삭제
    private fun remoceRecord(position: Int) {
        if (position >= 0 && position < wordRecordList.size) {
            wordRecordList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearList() {
        wordRecordList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }
}
