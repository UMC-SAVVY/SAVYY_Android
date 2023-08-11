package com.example.savvy_android.home.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.databinding.ItemHomeBinding
import com.example.savvy_android.diary.activity.DiaryDetailActivity
import com.example.savvy_android.diary.adapter.DiaryListAdapter
import com.example.savvy_android.home.data.HomeListResult

class HomeAdapter(
    private val context: Context,
    private var homeList: ArrayList<HomeListResult>,
) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class HomeViewHolder(binding: ItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var title = binding.itemHomeTitle
        var imgLayout = binding.itemHomeImgLayout
        var img = binding.itemHomeViewImg
        var date = binding.itemHomeDate
        var tag = binding.itemHomeTag
        var user = binding.itemHomeUser
        var likeCount = binding.itemHomeLikeTv
        var commentCount = binding.itemHomeCommentTv
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeViewHolder {
        val binding =
            ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val data = homeList[position]
        holder.title.text = data.title
        holder.date.text = data.updated_at
        holder.user.text = data.nickname
        holder.likeCount.text = data.likes_count.toString()
        holder.commentCount.text = data.comments_count.toString()
        if (data.thumbnail?.isNotEmpty() == true) {
            holder.imgLayout.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(data.thumbnail)
                .into(holder.img)
        } else {
            holder.imgLayout.visibility = View.GONE
        }
        if (data.hashtag?.isEmpty() == false) {
            holder.tag.visibility = View.VISIBLE
            var tempHashtag = ""
            for (item in data.hashtag!!) {
                tempHashtag += "# ${item.tag} "
            }
            holder.tag.text = tempHashtag
        } else {
            holder.tag.visibility = View.GONE
        }

        // 아이템 클릭 이벤트 (다이어리 상세보기)
        holder.itemView.setOnClickListener {
            val mIntent = Intent(holder.itemView.context, DiaryDetailActivity::class.java)
            mIntent.putExtra("diaryID", data.id)
            holder.itemView.context.startActivity(mIntent)
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = homeList.size

    // 데이터 추가
    fun addDiary(blockData: HomeListResult) {
        homeList.add(blockData)
        notifyItemInserted(homeList.size)
    }

    // 데이터 삭제
    private fun removeDiary(position: Int) {
        if (position >= 0 && position < homeList.size) {
            homeList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearList() {
        homeList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }

    // 마지막 아이템의 다이어리 id 반환
    fun getLastItemId(): Int {
        return homeList[itemCount - 1].id
    }

    // 클릭한 아이템 다이어리 view API

}
