package com.example.savvy_android.diary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemDiaryTagBinding
import com.example.savvy_android.diary.data.detail.DiaryHashtag

class Make4Adapter(
    context: Context,
    private val hashtagData: ArrayList<DiaryHashtag>,
) : RecyclerView.Adapter<Make4Adapter.DiaryHashtagViewHolder>() {
    private val imm =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // 각 뷰들을 binding 사용하여 View 연결
    inner class DiaryHashtagViewHolder(binding: ItemDiaryTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var text = binding.hashtagEdit
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DiaryHashtagViewHolder {
        val binding =
            ItemDiaryTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryHashtagViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: DiaryHashtagViewHolder, position: Int) {
        val data = hashtagData[holder.adapterPosition]

        if (!holder.text.hasFocus()) {
            holder.text.requestFocus() // EditText에 포커스를 주기 위한 메소드

            // 필요에 따라 키보드를 자동으로 올릴 수도 있음
            imm.showSoftInput(holder.text, InputMethodManager.SHOW_IMPLICIT)
        }

        holder.text.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val tempText = holder.text.text
                if (tempText.isNotEmpty()) {
                    // 입력된 내용을 데이터에 저장하고, 힌트(placeholder) 비우기
                    data.tag = tempText.toString()
                    holder.text.hint = ""
                } else {
                    // 내용이 비어있을 경우, 특정 처리를 수행
                    removeHashtag(position)
                }
            }
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = hashtagData.size

    fun addHashtag(insertData: DiaryHashtag) {
        hashtagData.add(insertData)
        notifyItemInserted(hashtagData.size)
    }

    // CheckList 추가
    fun removeHashtag(position: Int) {
        if (position >= 0 && position < hashtagData.size) {
            hashtagData.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
