package com.example.savvy_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemDiaryPlanBinding

class DiaryPlanListAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<DiaryPlanListAdapter.ViewHolder>() {

    val clickedPositions = mutableSetOf<Int>()


    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private val binding: ItemDiaryPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val isClicked: Boolean
            get() = adapterPosition in clickedPositions

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String, position: Int) {

            // 아이템 삭제 버튼 클릭 시 색 바뀜
            binding.checkBtn.setOnClickListener {
                toggleClicked(position)
                updateCheckmark()
            }
        }

        private fun toggleClicked(position: Int) {
            if (position in clickedPositions) {
                clickedPositions.remove(position)
            } else {
                clickedPositions.add(position)
            }
        }

        private fun updateCheckmark() {
            if (isClicked) {
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