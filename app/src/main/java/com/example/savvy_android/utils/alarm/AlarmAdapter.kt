package com.example.savvy_android.utils.alarm

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemAlarmBinding

class AlarmAdapter(
    private var alarmList: ArrayList<AlarmResult>,
) :
    RecyclerView.Adapter<AlarmAdapter.MypageBlockViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class MypageBlockViewHolder(binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val img = binding.alarmImg
        var comment = binding.alarmComment
        var date = binding.alarmDate
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MypageBlockViewHolder {
        val binding =
            ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MypageBlockViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: MypageBlockViewHolder, position: Int) {
        // 계획서 연결 or 마이페이지 연결구분에 따라 삭제 레이아웃 시각화 설정
        val data = alarmList[position]

        // 알림 타입에 맞게 구분
        when (data.type) {
            "like" -> {
                holder.img.setImageResource(R.drawable.ic_alarm_heart)
            }

            "comment", "reply" -> {
                holder.img.setImageResource(R.drawable.ic_alarm_comment)
            }
        }
        holder.date.text = data.updated_at
        holder.comment.text = data.body

        // 특정 문자 bold
        val textData: String = holder.comment.text.toString()
        val builder = SpannableStringBuilder(textData)
        val nameLength = data.nickname.length
        val boldSpan = StyleSpan(Typeface.BOLD)
        builder.setSpan(boldSpan, 0, nameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = alarmList.size

    // 데이터 추가
    fun addAlarm(blockData: AlarmResult) {
        alarmList.add(blockData)
        notifyItemInserted(alarmList.size)
    }
}
