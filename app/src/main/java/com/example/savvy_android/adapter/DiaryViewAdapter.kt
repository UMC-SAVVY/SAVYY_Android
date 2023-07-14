package com.example.savvy_android.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.data.DiaryViewItemData
import com.example.savvy_android.databinding.ItemDiaryViewBinding


class DiaryViewAdapter(
    private var diaryViewData: ArrayList<DiaryViewItemData>,
) :
    RecyclerView.Adapter<DiaryViewAdapter.DiaryViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class DiaryViewHolder(binding: ItemDiaryViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var text = binding.itemDiaryViewTv
        var imageLayout = binding.itemDiaryViewImgLayout
        var image = binding.itemDiaryViewImg
        var placeCard = binding.itemDiaryPlaceCard
        var placeIcon = binding.itemDiaryPlaceIcon
        var placeName = binding.itemDiaryPlaceName
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DiaryViewHolder {
        val binding =
            ItemDiaryViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val data = diaryViewData[position]
        val hasText = data.isText
        val hasPlace = data.hasPlace
        val placeName = data.placeName
        val placeUrl = data.placeUrl

        // 텍스트 or 이미지 인지
        if (hasText) {
            // 텍스트 경우
            holder.text.visibility = View.VISIBLE   // 텍스트 보이게
            holder.imageLayout.visibility = View.GONE // 이미지 안 보이게
            holder.text.text = data.text
        } else {
            // 이미지 경우
            holder.imageLayout.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(data.Image)
                .into(holder.image)

            // 장소가 장소 저장함에 있을 때
            if (hasPlace) {
                holder.placeIcon.setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.main
                    )
                )
            } else {
                holder.placeIcon.setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.basic_gray
                    )
                )
            }

            // 장소 이름
            holder.placeName.text = placeName

            // 장소 cardView 클릭 이벤트
            holder.placeCard.setOnClickListener {
                // 구글맵과 연결
            }
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = diaryViewData.size

    // 데이터 추가
    fun addPlan(insertData: DiaryViewItemData) {
        diaryViewData.add(insertData)
        notifyItemInserted(diaryViewData.size)
        Log.e("TEST", "$diaryViewData")
    }

    // 데이터 삭제
    private fun removePlan(position: Int) {
        if (position >= 0 && position < diaryViewData.size) {
            diaryViewData.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
