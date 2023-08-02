package com.example.savvy_android.plan.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemPlanMakeChecklistBinding
import com.example.savvy_android.plan.data.Checklist

class MakeCheckListAdapter(private val items: MutableList<Checklist>) :
    RecyclerView.Adapter<MakeCheckListAdapter.CheckListViewHolder>() {

    inner class CheckListViewHolder(private val binding: ItemPlanMakeChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Checklist) {
            binding.checklistEdit.setText(item.contents)

            binding.checklistEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    items[position].contents = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            val content = binding.checklistEdit.text.toString()
            val updatedChecklist = Checklist(
                id = item.id,
                contents = content,
                is_checked = item.is_checked
            )

            items[adapterPosition] = updatedChecklist

            // 아이템 삭제 버튼 클릭 시 해당 위치의 아이템을 제거하고 새로고침
            binding.icRemoveChecklist.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlanMakeChecklistBinding.inflate(inflater, parent, false)
        return CheckListViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    // CheckList 추가
    fun addItem(item: Checklist) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun getDataList(): MutableList<Checklist> {
        return items
    }

}
