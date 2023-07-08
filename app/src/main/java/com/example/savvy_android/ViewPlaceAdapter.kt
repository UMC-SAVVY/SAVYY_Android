package com.example.savvy_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemViewPlaceBinding

class ViewPlaceAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<ViewPlaceAdapter.ViewHolder>() {
    private val viewCheckListMap: MutableMap<Int, ViewChecklistAdapter> = mutableMapOf()


    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, position)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: ItemViewPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var viewChecklistAdapter: ViewChecklistAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String, position: Int) {

            // checkList RecyclerView 설정
            viewChecklistAdapter = ViewChecklistAdapter(mutableListOf("", ""))
            viewCheckListMap[position] = viewChecklistAdapter
            binding.recyclerviewViewChecklist.adapter = viewChecklistAdapter
            binding.recyclerviewViewChecklist.layoutManager = LinearLayoutManager(itemView.context)
        }
    }

    // PlaceAdd 추가
    fun addItem(item: String) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }
}
