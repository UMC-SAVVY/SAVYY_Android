package com.example.savvy_android

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemPlanBinding


class PlanListAdapter(private var planList: ArrayList<PlanItemData>, private val myName: String,private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<PlanListAdapter.PlanViewHolder>() {
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
    ): PlanListAdapter.PlanViewHolder {
        val binding =
            ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: PlanListAdapter.PlanViewHolder, position: Int) {
        val data = planList[position]
        holder.title.text = data.title
        holder.date.text = data.date
        holder.user.text = if (data.user == myName) "" else data.user

        // 숨겨진 삭제 버튼 클릭 이벤트
        holder.hideO.setOnClickListener {
            val dialog = PlanDeleteDialogFragment()

            // 버튼 클릭 이벤트 설정
            dialog.setButtonClickListener(object : PlanDeleteDialogFragment.OnButtonClickListener {
                override fun onDialogPlanBtnOClicked() {
                    removePlan(position)
                }
                override fun onDialogPlanBtnXClicked() {
                    resetHideX(holder, position)
                }
            })
            dialog.show(fragmentManager, "PlanDeleteDialog")
        }

        // 아이템(계획서) 클릭 이벤트
        holder.item.setOnClickListener {
//            val mIntent = Intent(holder.item.context, 여행계획서 작성 페이지 파일)
//            startActivity(mIntent)
            // 임시
            Toast.makeText(holder.item.context, "여행 계획서 눌림", Toast.LENGTH_SHORT).show()
        }

        holder.hideO.isClickable = false // 초기 클릭 가능 상태 설정
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = planList.size

    // 데이터 추가
    fun addPlan(storyData: PlanItemData) {
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
