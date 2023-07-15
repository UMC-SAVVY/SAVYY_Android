package com.example.savvy_android.diary.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.utils.place.PlaceAddActivity
import com.example.savvy_android.databinding.ItemDiaryPhotoBinding

class DiaryPhotoAdapter(private val photoList: MutableList<PhotoItem>) :
    RecyclerView.Adapter<DiaryPhotoAdapter.ItemDiaryPhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDiaryPhotoViewHolder {
        val binding = ItemDiaryPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemDiaryPhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemDiaryPhotoViewHolder, position: Int) {
        val photoItem = photoList[position]
        holder.bind(photoItem)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    inner class ItemDiaryPhotoViewHolder(private val binding: ItemDiaryPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photoItem: PhotoItem) {
            // 이미지 설정 등 필요한 작업 수행
            Glide.with(binding.root)
                .load(photoItem.photoUrl)
                .centerCrop()
                .into(binding.photoImage)

            // 아이템 삭제 버튼 클릭 시 해당 위치의 아이템을 제거하고 새로고침
            binding.deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    photoList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            // 장소추가 버튼 클릭 시 장소 추가 액티비티로 이동
            binding.addPlaceBtn.setOnClickListener {
                val intent = Intent(binding.root.context, PlaceAddActivity::class.java)
                binding.root.context.startActivity(intent)
            }
        }
    }
}

data class PhotoItem(val photoUrl: String)