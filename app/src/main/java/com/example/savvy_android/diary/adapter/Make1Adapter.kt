package com.example.savvy_android.diary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemDiaryPlanBinding
import com.example.savvy_android.plan.data.list.PlanListResult

class Make1Adapter(
    private val recyclerView: RecyclerView,
    private var planList: ArrayList<PlanListResult>,
) :
    RecyclerView.Adapter<Make1Adapter.ViewHolder>() {
    var isClickedPosition: Int = -1

    // 각 뷰들을 binding 사용하여 View 연결
    inner class ViewHolder(binding: ItemDiaryPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var title = binding.travelPlanName
        var date = binding.travelPlanDate
        var checkBtn = binding.checkBtn
        var borderCircle = binding.borderCircleGray
        var checkMark = binding.checkmarkGray
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemDiaryPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = planList[holder.adapterPosition]
        holder.title.text = data.title
        holder.date.text = data.updated_at

        // 아이템 삭제 버튼 클릭 시 색 바뀜
        holder.checkBtn.setOnClickListener {
            isClickedPosition = if (isClickedPosition == -1) {
                checkmarkO(holder.adapterPosition, recyclerView)
                holder.adapterPosition
            } else {
                if (holder.adapterPosition == isClickedPosition) {
                    checkmarkX(holder.adapterPosition, recyclerView)
                    -1
                } else {
                    checkmarkX(isClickedPosition, recyclerView)
                    checkmarkO(holder.adapterPosition, recyclerView)
                    holder.adapterPosition
                }
            }
        }
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return planList.size
    }

    // 데이터 추가
    fun addPlan(insertData: PlanListResult) {
        planList.add(insertData)
        notifyItemInserted(planList.size)
    }

    fun clearList() {
        planList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }

    private fun checkmarkO(position: Int, recyclerView: RecyclerView) {
        // 클릭 시 Drawable 변경
        val changeHolder = recyclerView.findViewHolderForAdapterPosition(position) as ViewHolder
        changeHolder.borderCircle.setImageResource(R.drawable.border_circle_main)
        changeHolder.checkMark.setImageResource(R.drawable.ic_checkmark_main)
    }

    fun checkmarkX(position: Int, recyclerView: RecyclerView) {
        // 클릭 시 Drawable 변경
        val changeHolder = recyclerView.findViewHolderForAdapterPosition(position) as ViewHolder
        changeHolder.borderCircle.setImageResource(R.drawable.border_circle_gray)
        changeHolder.checkMark.setImageResource(R.drawable.ic_checkmark_gray)
    }
}