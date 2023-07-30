package com.example.savvy_android.plan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemPlanDetailChecklistBinding
import com.example.savvy_android.plan.data.Checklist

class DetailChecklistAdapter(private val items: MutableList<Checklist>) :
    RecyclerView.Adapter<DetailChecklistAdapter.ViewCheckListViewHolder>() {

    inner class ViewCheckListViewHolder(private val binding: ItemPlanDetailChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Checklist) {

            binding.travelPlanViewChecklistTv.text = item.contents

            binding.check.setOnClickListener {
                // 클릭 시 Drawable 변경
                item.is_checked = if (item.is_checked == 1) 0 else 1
                if (item.is_checked == 1) {
                    // 클릭 시 Drawable 변경
                    binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.border_circle_main)
                    binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_main))
                } else {
                    // 다시 클릭 시 원래의 Drawable로 복원
                    binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.border_circle_gray)
                    binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_gray))
                }
            }
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCheckListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlanDetailChecklistBinding.inflate(inflater, parent, false)
        return ViewCheckListViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewCheckListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

}
