package com.example.savvy_android.diary.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemDiaryMakeBinding
import com.example.savvy_android.diary.data.DiaryDetailItemData
import com.example.savvy_android.diary.activity.DiaryMake3Activity
import com.example.savvy_android.utils.place.PlaceAddActivity


class Make3Adapter(
    private var diaryViewData: ArrayList<DiaryDetailItemData>,
) :
    RecyclerView.Adapter<Make3Adapter.DiaryViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class DiaryViewHolder(binding: ItemDiaryMakeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var text = binding.itemDiaryViewTv
        var imageLayout = binding.itemDiaryViewImgLayout
        var image = binding.itemDiaryViewImg
        var imageDelete = binding.itemDiaryDeleteCv
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
            ItemDiaryMakeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val data = diaryViewData[position]
        val hasText = data.isText
        val hasPlace = data.hasPlace

        // 텍스트 or 이미지 인지
        if (hasText) {
            // 텍스트 경우
            holder.text.visibility = View.VISIBLE   // 텍스트 보이게
            holder.imageLayout.visibility = View.GONE // 이미지 안 보이게
        } else {
            // 이미지 경우
            holder.text.visibility = View.GONE   // 텍스트 안 보이게
            holder.imageLayout.visibility = View.VISIBLE    // 이미지 보이게
            Glide.with(holder.itemView)
                .load(data.image)
                .into(holder.image)

            // 장소가 장소 저장함에 있을 때
            if (hasPlace!!) {
                holder.placeIcon.setImageResource(R.drawable.ic_map)
                holder.placeName.text = data.placeName  // 장소 이름
            } else {
                holder.placeIcon.setImageResource(R.drawable.ic_plus_round)
                holder.placeName.text = "장소 추가하기"
            }

            // 이미지 삭제 클릭 이벤트
            holder.imageDelete.setOnClickListener {
                removeDiary(position)
            }

            // 장소 cardView 클릭 이벤트
            holder.placeCard.setOnClickListener {
                if (hasPlace) {
                    holder.placeIcon.setImageResource(R.drawable.ic_plus_round)
                    holder.placeName.text = "장소 추가하기"
                } else {
                    val intent = Intent(holder.itemView.context, PlaceAddActivity::class.java)
                    (holder.itemView.context as DiaryMake3Activity).startActivityForResult(intent, 100)
                }
            }
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = diaryViewData.size

    // 데이터 추가
    fun addDiary(insertData: DiaryDetailItemData) {
        diaryViewData.add(insertData)
        notifyItemInserted(diaryViewData.size)
        Log.e("TEST", "$diaryViewData")
    }

    // 데이터 삭제
    private fun removeDiary(position: Int) {
        if (position >= 0 && position < diaryViewData.size) {
            diaryViewData.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
