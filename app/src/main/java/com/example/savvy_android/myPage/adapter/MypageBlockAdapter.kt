package com.example.savvy_android.myPage.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemBlockBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.myPage.data.MyPageBlockResult

class MypageBlockAdapter(
    private var blockList: ArrayList<MyPageBlockResult>,
) :
    RecyclerView.Adapter<MypageBlockAdapter.MypageBlockViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class MypageBlockViewHolder(binding: ItemBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var name = binding.blockNameTv
        val cancel = binding.blocKCancel
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MypageBlockViewHolder {
        val binding =
            ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MypageBlockViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: MypageBlockViewHolder, position: Int) {
        // 계획서 연결 or 마이페이지 연결구분에 따라 삭제 레이아웃 시각화 설정
        holder.name.text = blockList[position].nickname
        holder.cancel.setOnClickListener {
            // 커스텀 Toast 메시지 생성
            val inflater = LayoutInflater.from(holder.itemView.context)
            val toastBinding = LayoutToastBinding.inflate(inflater)
            toastBinding.toastMessage.text = "성공적으로 차단이 해제되었습니다."

            val toast = Toast(holder.itemView.context)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastBinding.root

            toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정

            toast.show()
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = blockList.size

    // 데이터 추가
    fun addBlock(blockData: MyPageBlockResult) {
        blockList.add(blockData)
        notifyItemInserted(blockList.size)
    }

    fun clearList() {
        blockList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }
}
