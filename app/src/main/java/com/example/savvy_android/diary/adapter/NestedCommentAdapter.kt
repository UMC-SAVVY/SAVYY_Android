package com.example.savvy_android.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemNestedCommentBinding
import com.example.savvy_android.diary.data.NestedCommentItemData

class NestedCommentAdapter(
    private val items: MutableList<NestedCommentItemData>,
    private val nestedOptionClickListener: OnNestedOptionClickListener,
    private val commentPosition: Int // Add commentPosition as a parameter
) : RecyclerView.Adapter<NestedCommentAdapter.NestedCommentViewHolder>() {

    private lateinit var recyclerView: RecyclerView // RecyclerView 변수를 추가

    inner class NestedCommentViewHolder(private val binding: ItemNestedCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: NestedCommentItemData) {
            binding.diaryNestedCommentName.text = item.userName
            binding.nestedCommentContentTv.text = item.commentContent
            binding.nestedCommentUpdateDate.text = item.date

            // 옵션 버튼에 클릭 리스너 설정
            binding.nestedOption.setOnClickListener {
                nestedOptionClickListener.onNestedOptionClick(commentPosition, adapterPosition)
            }
        }

        fun showEditText() {
            // TextView를 숨기고 EditText를 보여줌
            binding.nestedCommentContentTv.visibility = View.INVISIBLE
            binding.nestedCommentModifyEdit.visibility = View.VISIBLE
            binding.nestedCommentModifyEdit.setText(binding.nestedCommentContentTv.text)
            binding.nestedCommentModifyEdit.requestFocus() // EditText에 포커스를 주어 키보드가 나타나도록 함
        }

        fun showTextView() {
            // EditText의 내용을 TextView에 적용하고 TextView를 보여주고 EditText를 숨김
            val editedText = binding.nestedCommentModifyEdit.text.toString()
            binding.nestedCommentContentTv.text = editedText
            binding.nestedCommentModifyEdit.visibility = View.INVISIBLE
            binding.nestedCommentContentTv.visibility = View.VISIBLE
        }

    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedCommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNestedCommentBinding.inflate(inflater, parent, false)
        return NestedCommentViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: NestedCommentViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    // Nested Comment 추가
    fun addItem(item: NestedCommentItemData) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    // 댓글 아이템을 position에 해당하는 뷰 홀더를 반환하는 함수
    fun getNestedCommentViewHolder(position: Int): NestedCommentAdapter.NestedCommentViewHolder? {
        if (position in 0 until itemCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            if (viewHolder is NestedCommentAdapter.NestedCommentViewHolder) {
                return viewHolder
            }
        }
        return null
    }

    fun showEditText(nestedCommentPosition: Int) {
        // 댓글 수정 시 해당 NestedCommentViewHolder의 showEditText() 함수 호출
        val viewHolder = getNestedCommentViewHolder(nestedCommentPosition)
        viewHolder?.showEditText()
    }


    interface OnNestedOptionClickListener {
        fun onNestedOptionClick(commentPosition: Int, nestedCommentPosition: Int)
    }
}
