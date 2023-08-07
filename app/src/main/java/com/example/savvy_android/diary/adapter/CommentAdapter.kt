package com.example.savvy_android.diary.adapter

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemCommentBinding
import com.example.savvy_android.diary.activity.DiaryCommentActivity
import com.example.savvy_android.diary.data.comment.CommentRequest
import com.example.savvy_android.diary.data.comment.CommentResult
import com.example.savvy_android.diary.data.comment.NestedCommentRequest
import com.example.savvy_android.diary.data.comment.NestedCommentResponse
import com.example.savvy_android.diary.data.comment.NestedCommentResult
import com.example.savvy_android.diary.service.NestedCommentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentAdapter(
    private val context: Context,
    private val items: MutableList<Any>,
    private val optionClickListener: OnOptionClickListener,
    private val nestedOptionClickListener: NestedCommentAdapter.OnNestedOptionClickListener
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val nestedCommentMap: MutableMap<Int, MutableList<NestedCommentRequest>> = mutableMapOf()
    private val nestedCommentCounts: MutableList<Int> = mutableListOf()
    private lateinit var recyclerView: RecyclerView // RecyclerView 변수를 추가
    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의
    private val expandedItems: MutableMap<Int, Boolean> = mutableMapOf()

    // 아직 commentID 안 받아옴

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var nestedCommentAdapter: NestedCommentAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Any) {

            val nestedComments: MutableList<NestedCommentRequest> = mutableListOf()

            nestedCommentAdapter = NestedCommentAdapter(mutableListOf(), nestedOptionClickListener, adapterPosition)
            nestedCommentMap[adapterPosition] = nestedComments
            binding.recyclerviewNestedComment.adapter = nestedCommentAdapter
            binding.recyclerviewNestedComment.layoutManager = LinearLayoutManager(itemView.context)

            if (item is CommentRequest) {
                // CommentRequest 처리
                binding.commentContentTv.text = item.content

            } else if (item is CommentResult) {
                binding.commentContentTv.text = item.content
                binding.commentNum.text = item.reply_count
                binding.diaryCommentName.text = item.nickname
                binding.commentUpdateDate.text = item.updated_at

                setNestedComments(item.reply_List)
            }

            val isExpanded = expandedItems[adapterPosition] ?: false
            if (isExpanded) {
                binding.nestedComment.visibility = View.VISIBLE
                binding.viewNestedCommentBtn.visibility = View.GONE
            } else {
                binding.nestedComment.visibility = View.GONE
                binding.viewNestedCommentBtn.visibility = View.VISIBLE
            }

            binding.viewNestedCommentBtn.setOnClickListener {
                // 아이템 상태 변경
                expandedItems[adapterPosition] = true
                notifyDataSetChanged()

                val commentId = if (item is CommentResult) item.id else -1
                Log.d("test", "commentID: $commentId")
            }

            binding.commentBtn.setOnClickListener {
                val newComment = binding.commentEdit.text.toString()
                if (newComment.isNotBlank()) {
                    val nestedCommentRequest = NestedCommentRequest(
                        comment_id = if (item is CommentResult) item.id else -1, // 여기서 수정
                        content = newComment
                    )
                    Log.d("test2", "commentID: ${nestedCommentRequest.comment_id}") // 수정된 부분

                    nestedCommentAdapter.addItem(nestedCommentRequest)
                    binding.commentEdit.text.clear() // 댓글 입력창 비우기
                    incrementCommentNum(adapterPosition) // 이 댓글 아이템에 대한 대댓글 개수 증가

                    nestedCommentMakeAPI(nestedCommentRequest)

                    // 대댓글 추가 후 댓글 목록을 갱신하는 함수를 호출
                    val activity = context as DiaryCommentActivity
                    activity.refreshCommentListAfterAddingNestedComment(activity.getDiaryId())

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

        private fun setNestedComments(nestedComments: MutableList<NestedCommentResult>) {
            nestedCommentAdapter.addAllItems(nestedComments)
        }

        private fun incrementCommentNum(position: Int) {
            if (position in 0 until nestedCommentCounts.size) {
                nestedCommentCounts[position]++
                updateCommentNum(position)
            }
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
    fun addItem(item: Any) {
        items.add(item)
        nestedCommentCounts.add(0) // 새로운 댓글 아이템에 대댓글 개수 0으로 초기화
        notifyDataSetChanged() // 모든 아이템을 갱신
    }
    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
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
        if (position in 0 until items.size) {
            items.removeAt(position)
            // nestedCommentCounts 리스트도 해당 인덱스 제거
            if (position in 0 until nestedCommentCounts.size) {
                nestedCommentCounts.removeAt(position)
            }
            notifyItemRemoved(position)
        }
    }


    // 대댓글 삭제
    fun removeNestedComment(commentPosition: Int, nestedCommentPosition: Int) {
        val nestedComments: MutableList<NestedCommentRequest> = nestedCommentMap[commentPosition] ?: mutableListOf()
        if (nestedCommentPosition in 0 until nestedComments.size) {
            nestedComments.removeAt(nestedCommentPosition)
            nestedCommentMap[commentPosition] = nestedComments
            nestedCommentCounts[commentPosition]-- // 대댓글 개수 감소
            notifyItemChanged(commentPosition)
        }
    }

    // Comment 수정하기 (text->editText)
    fun showCommentEditText(position: Int) {
        // 댓글 수정 시 해당 CommentViewHolder의 showEditText() 함수 호출
        val viewHolder = getCommentViewHolder(position)
        viewHolder?.showEditText()
    }


   // // Nested Comment 수정하기 (text->editText)
   // fun showNestedCommentEditText(commentPosition: Int, nestedCommentPosition: Int) {
   //     val nestedCommentAdapter = nestedCommentMap[commentPosition]
   //     nestedCommentAdapter?.showEditText(nestedCommentPosition)
   // }


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

    fun addAllItems(items: MutableList<CommentResult>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }


    // 서버로 작성 데이터 전송하는 함수
    private fun nestedCommentMakeAPI(nestedCommentRequest: NestedCommentRequest) {
        // 서버 주소
        sharedPreferences = context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val nestedCommentService = retrofit.create(NestedCommentService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        nestedCommentService.nestedCommentMake(serverToken, nestedCommentRequest).enqueue(object :
            Callback<NestedCommentResponse> {
            override fun onResponse(call: Call<NestedCommentResponse>, response: Response<NestedCommentResponse>) {
                if (response.isSuccessful) {
                    val nestedCommentMakeResponse = response.body()
                    val isSuccess = nestedCommentMakeResponse?.isSuccess
                    val code = nestedCommentMakeResponse?.code
                    val message = nestedCommentMakeResponse?.message
                    if (nestedCommentMakeResponse != null && nestedCommentMakeResponse.isSuccess) {
                        // 전송 성공
                        Log.d("DiaryCommentActivity - 답글 전송", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")
                    } else {
                        // 전송 실패
                        Log.d("DiaryCommentActivity - 답글 전송", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("DiaryCommentActivity - 답글 전송", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<NestedCommentResponse>, t: Throwable) {
                // 통신 실패
                Log.d("DiaryCommentActivity - 답글 전송", "통신 실패 - ${t.message}")
            }
        })
    }


}

// !해결할 것!
// 현재 nestedComment 아이템 삭제 안됨