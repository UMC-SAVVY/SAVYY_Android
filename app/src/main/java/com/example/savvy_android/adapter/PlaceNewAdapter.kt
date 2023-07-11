package com.example.savvy_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.data.PlaceNewItemData
import com.example.savvy_android.databinding.ItemPlaceNewBinding

class PlaceNewAdapter(private var searchList: ArrayList<PlaceNewItemData>) :
    RecyclerView.Adapter<PlaceNewAdapter.PlaceNewViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class PlaceNewViewHolder(binding: ItemPlaceNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var name = binding.searchNameTv
        var address = binding.searchAddressTv
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceNewAdapter.PlaceNewViewHolder {
        val binding =
            ItemPlaceNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceNewViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: PlaceNewAdapter.PlaceNewViewHolder, position: Int) {

    }

    // 리스트의 수 count
    override fun getItemCount(): Int = searchList.size

    // 데이터 추가
    fun addPost(postData: PlaceNewItemData) {
        searchList.add(postData)
        notifyItemInserted(searchList.size)
    }
}
