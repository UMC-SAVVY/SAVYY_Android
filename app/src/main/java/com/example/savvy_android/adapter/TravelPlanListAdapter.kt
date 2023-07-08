package com.example.savvy_android.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.data.TravelPlanItemData
import com.example.savvy_android.activity.TravelPlanViewActivity
import com.example.savvy_android.databinding.ItemPlanBinding
import com.example.savvy_android.dialog.TravelPlanDeleteDialogFragment


class TravelPlanListAdapter(
    private var planList: ArrayList<TravelPlanItemData>,
    private val myName: String,
    private val fragmentManager: FragmentManager,
) :
    RecyclerView.Adapter<TravelPlanListAdapter.PlanViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class PlanViewHolder(binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var item = binding.itemPlanIn
        var title = binding.itemPlanTitle
        var date = binding.itemPlanDate
        var user = binding.itemPlanUser
        var hideX = binding.itemPlanHideX
        var hideO = binding.itemPlanHideO
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlanViewHolder {
        val binding =
            ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val data = planList[position]
        holder.title.text = data.title
        holder.date.text = data.date
        holder.user.text = if (data.user == myName) "" else data.user

        // 숨겨진 삭제 버튼 클릭 이벤트
        holder.hideO.setOnClickListener {
            val dialog = TravelPlanDeleteDialogFragment()

            // 버튼 클릭 이벤트 설정
            dialog.setButtonClickListener(object :
                TravelPlanDeleteDialogFragment.OnButtonClickListener {
                override fun onDialogPlanBtnOClicked() {
                    removePlan(position)
                }

                override fun onDialogPlanBtnXClicked() {
                    resetHideX(holder, position)
                }
            })
            dialog.show(fragmentManager, "PlanDeleteDialog")
        }

        // 아이템(계획서) 보기 클릭 이벤트
        holder.item.setOnClickListener {
            val mIntent = Intent(holder.itemView.context, TravelPlanViewActivity::class.java)
            holder.itemView.context.startActivity(mIntent)
        }

        holder.hideO.isClickable = false // 초기 클릭 가능 상태 설정
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = planList.size

    // 데이터 추가
    fun addPlan(storyData: TravelPlanItemData) {
        planList.add(storyData)
        notifyItemInserted(planList.size)
    }

    // 데이터 삭제
    private fun removePlan(position: Int) {
        if (position >= 0 && position < planList.size) {
            planList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // 삭제 버튼 안 보이고, 작동 안 하도록
    private fun resetHideX(holder: PlanViewHolder, position: Int) {
        holder.hideO.isClickable = false
        holder.hideX.translationX = 0f
    }
}
