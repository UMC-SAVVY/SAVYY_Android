package com.example.savvy_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemViewDateOnlyBinding

class ViewDateOnlyAdapter(private val items: MutableList<String>) :
    RecyclerView.Adapter<ViewDateOnlyAdapter.ViewDateOnlyViewHolder>() {

    inner class ViewDateOnlyViewHolder(private val binding: ItemViewDateOnlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String) {
            // arrowDownBtn 클릭 이벤트 처리
            binding.arrowDownBtn.setOnClickListener {

                binding.arrowDownBtn.setOnClickListener {
                    val newView = LayoutInflater.from(binding.root.context).inflate(
                        R.layout.item_view_date_only,
                        binding.root,
                        false
                    )
                    binding.root.removeAllViews()
                    binding.root.addView(newView)
                }
            }
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewDateOnlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemViewDateOnlyBinding.inflate(inflater, parent, false)
        return ViewDateOnlyViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewDateOnlyViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }
}