package com.example.savvy_android.diary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemNestedCommentBinding

class NestedCommentAdapter(private val items: MutableList<String>) :
    RecyclerView.Adapter<NestedCommentAdapter.NestedCommentViewHolder>() {

    inner class NestedCommentViewHolder(private val binding: ItemNestedCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String) {
            binding.nestedCommentContentTv.text = item
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedCommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNestedCommentBinding.inflate(inflater, parent, false)
        return NestedCommentViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: NestedCommentViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    // Nested Comment 추가
    fun addItem(item: String) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}
