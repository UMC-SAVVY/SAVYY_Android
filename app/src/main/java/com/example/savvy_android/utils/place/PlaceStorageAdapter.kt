package com.example.savvy_android.utils.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemPlaceStorageBinding

class PlaceStorageAdapter(private var storageList: ArrayList<PlaceStorageItemData>) :
    RecyclerView.Adapter<PlaceStorageAdapter.PlaceStorageViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class PlaceStorageViewHolder(binding: ItemPlaceStorageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.storageNameTv
        val mapBtn = binding.storageMapBtn
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceStorageViewHolder {
        val binding =
            ItemPlaceStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceStorageViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(
        holder: PlaceStorageViewHolder,
        position: Int,
    ) {

    }

    // 리스트의 수 count
    override fun getItemCount(): Int = storageList.size

    // 데이터 추가
    fun addStorage(postData: PlaceStorageItemData) {
        storageList.add(postData)
        notifyItemInserted(storageList.size)
    }
}
