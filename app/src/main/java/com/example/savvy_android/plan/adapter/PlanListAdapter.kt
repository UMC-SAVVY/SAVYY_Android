package com.example.savvy_android.plan.adapter

import android.animation.ObjectAnimator
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.plan.PlanItemData
import com.example.savvy_android.databinding.ItemPlanBinding
import com.example.savvy_android.plan.dialog.PlanDeleteDialogFragment


class PlanListAdapter(
    private val recyclerView: RecyclerView,
    private var planList: ArrayList<PlanItemData>,
    private val myName: String,
    private val fragmentManager: FragmentManager,
    private val isPlan: Boolean,
) :
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
        var scrollHolder = binding.itemPlanArrow
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
        // 계획서 페이지 or 마이페이지 구분에 따라 스크롤 레이아웃 시각화 설정
        if (isPlan)
            holder.scrollHolder.visibility = View.VISIBLE
        else
            holder.scrollHolder.visibility = View.GONE

        val data = planList[holder.adapterPosition]
        holder.title.text = data.title
        holder.date.text = data.date
        holder.user.text = if (data.user == myName) "" else data.user

        // 숨겨진 삭제 버튼 클릭 이벤트
        holder.hideO.setOnClickListener {
            val dialog = PlanDeleteDialogFragment()

            // 다이얼로그 버튼 클릭 이벤트 설정
            dialog.setButtonClickListener(object :
                PlanDeleteDialogFragment.OnButtonClickListener {
                override fun onDialogPlanBtnOClicked() {
                    removePlan(holder.adapterPosition)
                }
                override fun onDialogPlanBtnXClicked() {
                    resetHideX(holder.adapterPosition, recyclerView)
                }
            })
            dialog.show(fragmentManager, "PlanDeleteDialog")
        }

        // 아이템 클릭 이벤트 (여행 계획서 보기)
        holder.itemView.setOnClickListener {
            if (hasSwipe) {
                resetHideX(hasSwipePosition, recyclerView)
            } else {
                val mIntent = Intent(holder.itemView.context, PlanDetailActivity::class.java)
                holder.itemView.context.startActivity(mIntent)
            }
        }

        holder.hideO.isClickable = false // 초기 클릭 가능 상태 설정

        // 계획서 title scroll
        holder.title.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                holder.title.viewTreeObserver.removeOnPreDrawListener(this)

                holder.title.apply {
                    setSingleLine()
                    marqueeRepeatLimit = -1
                    ellipsize = TextUtils.TruncateAt.MARQUEE
                    isSelected = true
                }
                return true
            }
        })

    }

    // 리스트의 수 count
    override fun getItemCount(): Int = planList.size

    // 데이터 추가
    fun addPlan(insertData: PlanItemData) {
        planList.add(insertData)
        notifyItemInserted(planList.size)
    }

    // 데이터 삭제
    private fun removePlan(position: Int) {
        if (position >= 0 && position < planList.size) {
            planList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    // swipe 상태인 아이템 존재 유무
    companion object {
        var hasSwipe: Boolean = false
        var hasSwipePosition: Int = 0

        // 삭제 버튼 안 보이고, 작동 안 하도록
        fun resetHideX(position: Int, recyclerView: RecyclerView) {
            // 움직일 아이템의 holder
            val changeHolder =
                recyclerView.findViewHolderForAdapterPosition(position) as? PlanViewHolder
            if (changeHolder != null) {
                changeHolder.hideO.isClickable = false // 클릭 불가능
                // 애니메이션 추가
                val animator = ObjectAnimator.ofFloat(
                    changeHolder.hideX,
                    "translationX",
                    changeHolder.hideX.translationX,    // changeHolder.hideX의 현재 translationX 값
                    0f  // 애니메이션의 종료 값
                )
                animator.duration = 200 // 애니메이션 지속 시간 (밀리초)
                animator.start()
                hasSwipe = false    // swipe된 아이템 없음
            }
        }
    }
}
