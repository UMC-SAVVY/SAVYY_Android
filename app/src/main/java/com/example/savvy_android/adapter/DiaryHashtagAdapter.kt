package com.example.savvy_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemDiaryHashtagBinding

class DiaryHashtagAdapter(private val items: MutableList<String>) :
    RecyclerView.Adapter<DiaryHashtagAdapter.DiaryHashtagViewHolder>() {

    inner class DiaryHashtagViewHolder(private val binding: ItemDiaryHashtagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isClicked = false

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String) {
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryHashtagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDiaryHashtagBinding.inflate(inflater, parent, false)
        return DiaryHashtagViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: DiaryHashtagViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    // CheckList 추가
    fun addItem(item: String) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}
