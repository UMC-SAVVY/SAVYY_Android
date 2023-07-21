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

class CommentAdapter(private val items: MutableList<String>,
                     private val optionClickListener: OnOptionClickListener
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val nestedCommentMap: MutableMap<Int, NestedCommentAdapter> = mutableMapOf()
    // 댓글 개수를 나타내는 변수

    private var comment_num: Int = 0

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var nestedCommentAdapter: NestedCommentAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String) {

            binding.commentContentTv.text = item

            nestedCommentAdapter = NestedCommentAdapter(mutableListOf())
            nestedCommentMap[position] = nestedCommentAdapter
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
                    nestedCommentAdapter.addItem(newComment)
                    binding.commentEdit.text.clear() // 댓글 입력창 비우기
                    incrementCommentNum() //댓글 하나 추가될때마다 +1
                }
            }

            //옵션 버튼에 클릭 리스너 설정
            binding.option.setOnClickListener {
                optionClickListener.onOptionClick(adapterPosition)
            }

            //댓글 edit 버튼 활성화
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
        fun incrementCommentNum() {
            comment_num++
            updateCommentNum()
        }

        private fun updateCommentNum() {
            // comment_num 값을 commentNum TextView에 설정
            binding.commentNum.text = comment_num.toString()
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

    // Nested Comment 추가
    fun addItem(item: String) {
        items.add(item)
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

    interface OnOptionClickListener {
        fun onOptionClick(position: Int)
    }

}