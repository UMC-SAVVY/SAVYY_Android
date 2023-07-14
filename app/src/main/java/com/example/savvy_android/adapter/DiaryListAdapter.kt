package com.example.savvy_android.adapter

import android.animation.ObjectAnimator
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.activity.DiaryViewActivity
import com.example.savvy_android.data.DiaryItemData
import com.example.savvy_android.databinding.ItemDiaryBinding
import com.example.savvy_android.dialog.DiaryDeleteDialogFragment
import com.example.savvy_android.dialog.TravelPlanDeleteDialogFragment


class DiaryListAdapter(
    private val recyclerView: RecyclerView,
    private var diaryList: ArrayList<DiaryItemData>,
    private val fragmentManager: FragmentManager,
) :
    RecyclerView.Adapter<DiaryListAdapter.DiaryViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class DiaryViewHolder(binding: ItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var item = binding.itemDiaryIn
        var title = binding.itemDiaryTitle
        var date = binding.itemDiaryDate
        var like = binding.itemDiaryLikeTv
        var comment = binding.itemDiaryCommentTv
        var hideX = binding.itemDiaryHideX
        var photoCard = binding.itemDiaryPhotoCv
        var photoImg = binding.itemDiaryPhotoIv
        var photoCount = binding.itemDiaryPhotoTv
        var hideODelete = binding.itemDiaryHideODelete
        var hideOShow = binding.itemDiaryHideOShow
        var showImg = binding.itemDiaryShowIv
        var showText = binding.itemDiaryShowTv
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DiaryViewHolder {
        val binding =
            ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val data = diaryList[holder.adapterPosition]
        holder.title.text = data.title
        holder.date.text = data.date
        holder.like.text = data.like.toString()
        holder.comment.text = data.comment.toString()
        if (data.photoCount > 0) {
            holder.photoCard.isVisible = true
            holder.photoCount.setBackgroundResource(if (data.photoCount == 1) 0 else R.color.gray50)
            holder.photoCount.text = if (data.photoCount == 1) "" else "+${data.photoCount}"
            Glide.with(holder.itemView)
                .load(data.photoUrl)
                .into(holder.photoImg)
        } else {
            holder.photoCard.isVisible = false
        }
        showResource(data.isShow, holder)

        // 숨겨진 공개 여부 버튼 클릭 이벤트
        holder.hideOShow.setOnClickListener {
            data.isShow = !(data.isShow)
            showResource(data.isShow, holder)
        }

        // 숨겨진 삭제 버튼 클릭 이벤트
        holder.hideODelete.setOnClickListener {
            val dialog = DiaryDeleteDialogFragment()

            // 다이얼로그 버튼 클릭 이벤트 설정
            dialog.setButtonClickListener(object :
                DiaryDeleteDialogFragment.OnButtonClickListener {
                override fun onDialogPlanBtnOClicked() {
                    removePlan(holder.adapterPosition)
                }

                override fun onDialogPlanBtnXClicked() {
                    resetHideX(holder.adapterPosition, recyclerView)
                }
            })
            dialog.show(fragmentManager, "PlanDeleteDialog")
        }

        // 아이템 클릭 이벤트 (다이어리 상세보기)
        holder.itemView.setOnClickListener {
            if (hasSwipe) {
                resetHideX(hasSwipePosition, recyclerView)
            } else {
                val mIntent = Intent(holder.itemView.context, DiaryViewActivity::class.java)
                holder.itemView.context.startActivity(mIntent)
            }
        }

        holder.hideODelete.isClickable = false // 초기 클릭 가능 상태 설정
        holder.hideOShow.isClickable = false // 초기 클릭 가능 상태 설정


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
    override fun getItemCount(): Int = diaryList.size

    // 데이터 추가
    fun addPlan(insertData: DiaryItemData) {
        diaryList.add(insertData)
        notifyItemInserted(diaryList.size)
    }

    // 데이터 삭제
    private fun removePlan(position: Int) {
        if (position >= 0 && position < diaryList.size) {
            diaryList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // 공개 비공개 여부에 따른 리소스
    private fun showResource(isShow: Boolean, holder: DiaryViewHolder) {
        if (isShow) {
            holder.hideOShow.setBackgroundResource(R.color.green)
            holder.showImg.setImageResource(R.drawable.ic_eye_on)
            holder.showText.text = "공개"
        } else {
            holder.hideOShow.setBackgroundResource(R.color.button_line)
            holder.showImg.setImageResource(R.drawable.ic_eye_off)
            holder.showText.text = "비공개"
        }
    }


    // swipe 상태인 아이템 존재 유무
    companion object {
        var hasSwipe: Boolean = false
        var hasSwipePosition: Int = 0

        // 숨겨진 레이어 안 보이고, 작동 안 하도록
        fun resetHideX(position: Int, recyclerView: RecyclerView) {
            // 움직일 아이템의 holder
            val changeHolder =
                recyclerView.findViewHolderForAdapterPosition(position) as? DiaryListAdapter.DiaryViewHolder
            if (changeHolder != null) {
                changeHolder.hideODelete.isClickable = false // 클릭 불가능
                changeHolder.hideODelete.isClickable = false // 클릭 불가능
                // 애니메이션 추가
                val animator = ObjectAnimator.ofFloat(
                    changeHolder.hideX,
                    "translationX",
                    changeHolder.hideX.translationX,    // changeHolder.hideX의 현재 translationX 값
                    0f  // 애니메이션의 종료 값
                )
                animator.duration = 400 // 애니메이션 지속 시간 (밀리초)
                animator.start()
                hasSwipe = false    // swipe된 아이템 없음
            }
        }
    }
}
