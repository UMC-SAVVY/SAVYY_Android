package com.example.savvy_android.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemNestedCommentBinding
import com.example.savvy_android.diary.data.comment.NestedCommentRequest
import com.example.savvy_android.diary.data.comment.NestedCommentResult

class NestedCommentAdapter(
    private val items: MutableList<Any>,
    private val nestedOptionClickListener: OnNestedOptionClickListener,
    private val commentPosition: Int // Add commentPosition as a parameter
) : RecyclerView.Adapter<NestedCommentAdapter.NestedCommentViewHolder>() {

    private lateinit var recyclerView: RecyclerView // RecyclerView 변수를 추가

    inner class NestedCommentViewHolder(private val binding: ItemNestedCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Any) {

            if (item is NestedCommentRequest) {
                // CommentRequest 처리
                binding.nestedCommentContentTv.text = item.content

            } else if (item is NestedCommentResult) {
                binding.nestedCommentContentTv.text = item.content
                binding.diaryNestedCommentName.text = item.nickname
                binding.nestedCommentUpdateDate.text = item.updated_at
            }


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
    fun addItem(item: NestedCommentRequest) {
        items.add(0, item) // 새 아이템을 리스트의 맨 앞에 추가
        notifyDataSetChanged() // 추가한 아이템을 화면에 갱신
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

    // NestedCommentAdapter 클래스 내부
    fun addAllItems(nestedComments: MutableList<NestedCommentResult>) {
        this.items.clear()
        this.items.addAll(nestedComments)
        notifyDataSetChanged()
    }


}
