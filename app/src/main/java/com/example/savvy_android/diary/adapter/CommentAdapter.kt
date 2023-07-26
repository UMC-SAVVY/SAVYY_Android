package com.example.savvy_android.diary.adapter

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemCommentBinding
import com.example.savvy_android.diary.data.CommentItemData
import com.example.savvy_android.diary.data.NestedCommentItemData

class CommentAdapter(
    private val items: MutableList<CommentItemData>,
    private val optionClickListener: OnOptionClickListener,
    private val nestedOptionClickListener: NestedCommentAdapter.OnNestedOptionClickListener
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val nestedCommentMap: MutableMap<Int, NestedCommentAdapter> = mutableMapOf()
    private val nestedCommentCounts: MutableList<Int> = mutableListOf()
    private lateinit var recyclerView: RecyclerView // RecyclerView 변수를 추가
    // RecyclerView 할당 메소드
    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var nestedCommentAdapter: NestedCommentAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: CommentItemData) {
            binding.diaryCommentName.text = item.userName
            binding.commentContentTv.text = item.commentContent
            binding.commentUpdateDate.text = item.date
            binding.commentNum.text = item.commentNum.toString()


            nestedCommentAdapter = NestedCommentAdapter(item.nestedComment, nestedOptionClickListener, adapterPosition)
            nestedCommentMap[adapterPosition] = nestedCommentAdapter
            binding.recyclerviewNestedComment.adapter = nestedCommentAdapter
            binding.recyclerviewNestedComment.layoutManager = LinearLayoutManager(itemView.context)

            binding.viewNestedCommentBtn.setOnClickListener {
                binding.line.visibility = View.GONE
                binding.viewNestedCommentBtn.visibility = View.GONE
                binding.nestedComment.visibility = View.VISIBLE
            }

            binding.commentBtn.setOnClickListener {
                val newComment = binding.commentEdit.text.toString()
                if (newComment.isNotBlank()) {
                    val nestedCommentItemData = NestedCommentItemData(
                        position = 0,
                        userName = "내가 쓴 댓글",
                        commentContent = newComment,
                        date = "2023.7.26"
                    )
                    nestedCommentAdapter.addItem(nestedCommentItemData)
                    binding.commentEdit.text.clear() // 댓글 입력창 비우기
                    incrementCommentNum(adapterPosition) // 이 댓글 아이템에 대한 대댓글 개수 증가
                }
            }

            // 옵션 버튼에 클릭 리스너 설정
            binding.option.setOnClickListener {
                optionClickListener.onOptionClick(adapterPosition)
            }

            // 댓글 edit 버튼 활성화
            binding.commentEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val isEnableState = binding.commentEdit.length() != 0
                    binding.commentBtn.isEnabled = isEnableState
                    btnStateBackground(isEnableState, binding.commentBtn)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        private fun incrementCommentNum(position: Int) {
            nestedCommentCounts[position]++
            updateCommentNum(position)
        }

        // 대댓글 개수 감소 함수
        fun decrementCommentNum(position: Int) {
            if (nestedCommentCounts[position] > 0) {
                nestedCommentCounts[position]--
                updateCommentNum(position)
            }
        }

        private fun updateCommentNum(position: Int) {
            // comment_num 값을 commentNum TextView에 설정
            binding.commentNum.text = nestedCommentCounts[position].toString()
        }

        fun showEditText() {
            // TextView를 숨기고 EditText를 보여줌
            binding.commentContentTv.visibility = View.INVISIBLE
            binding.commentModifyEdit.visibility = View.VISIBLE
            binding.commentModifyEdit.setText(binding.commentContentTv.text)
            binding.commentModifyEdit.requestFocus() // EditText에 포커스를 주어 키보드가 나타나도록 함
        }

        fun showTextView() {
            // EditText의 내용을 TextView에 적용하고 TextView를 보여주고 EditText를 숨김
            val editedText = binding.commentModifyEdit.text.toString()
            binding.commentContentTv.text = editedText
            binding.commentModifyEdit.visibility = View.INVISIBLE
            binding.commentContentTv.visibility = View.VISIBLE
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    // Comment 추가
    fun addItem(item: CommentItemData) {
        items.add(item)
        nestedCommentCounts.add(0) // 새로운 댓글 아이템에 대댓글 개수 0으로 초기화
        notifyItemInserted(items.size - 1)
    }

    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val buttonColor = if (able) {
            ContextCompat.getColor(button.context, R.color.main)
        } else {
            ContextCompat.getColor(button.context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }

    // 댓글 삭제
    fun removeCommentAtPosition(position: Int) {
        items.removeAt(position)
        nestedCommentCounts.removeAt(position)
        notifyItemRemoved(position)
    }


    // 대댓글 삭제
    fun removeNestedComment(commentPosition: Int, nestedCommentPosition: Int) {
        val commentItem = items[commentPosition]
        val nestedCommentList = commentItem.nestedComment
        if (nestedCommentList.size > nestedCommentPosition) {
            nestedCommentList.removeAt(nestedCommentPosition)

            // nestedCommentAdapter에게 해당 대댓글이 삭제되었음을 알림
            nestedCommentMap[commentPosition]?.notifyItemRemoved(nestedCommentPosition)

            // 대댓글이 삭제되었으므로 댓글의 대댓글 개수를 감소시킴
            val viewHolder = getCommentViewHolder(commentPosition)
            viewHolder?.decrementCommentNum(commentPosition)
        }
    }

    // Comment 수정하기 (text->editText)
    fun showCommentEditText(position: Int) {
        // 댓글 수정 시 해당 CommentViewHolder의 showEditText() 함수 호출
        val viewHolder = getCommentViewHolder(position)
        viewHolder?.showEditText()
    }


    // Nested Comment 수정하기 (text->editText)
    fun showNestedCommentEditText(commentPosition: Int, nestedCommentPosition: Int) {
        val nestedCommentAdapter = nestedCommentMap[commentPosition]
        nestedCommentAdapter?.showEditText(nestedCommentPosition)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    // 댓글 아이템을 position에 해당하는 뷰 홀더를 반환하는 함수
    private fun getCommentViewHolder(position: Int): CommentViewHolder? {
        if (position in 0 until itemCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            if (viewHolder is CommentViewHolder) {
                return viewHolder
            }
        }
        return null
    }


    interface OnOptionClickListener {
        fun onOptionClick(position: Int)
    }
}