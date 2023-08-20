package com.example.savvy_android.diary.adapter

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemNestedCommentBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.data.comment.NestedCommentModifyRequest
import com.example.savvy_android.diary.data.comment.NestedCommentModifyResponse
import com.example.savvy_android.diary.data.comment.NestedCommentRequest
import com.example.savvy_android.diary.data.comment.NestedCommentResult
import com.example.savvy_android.diary.service.NestedCommentModifyService
import com.example.savvy_android.init.errorCodeList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NestedCommentAdapter(
    private val context: Context,
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

            //답글 수정 입력 변화 이벤트 처리
            binding.nestedCommentModifyEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 이전 텍스트 변경 전 동작
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트 변경 중 동작
                    val textLength = s?.length ?: 0

                    //한글자라도 입력하면 'check'버튼의 색이 바뀜
                    if (textLength > 0) {
                        binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.ic_circle_main)
                        binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_white))
                    } else {
                        binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.ic_circle_gray)
                        binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_white))
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            // 버튼 클릭 리스너 설정
            binding.check.setOnClickListener {
                // 버튼을 눌렀을 때 처리할 로직 추가
                val commentText = binding.nestedCommentModifyEdit.text.toString()
                if (commentText.isNotEmpty()) {
                    // 버튼을 눌렀을 때 한 글자 이상 입력되었을 때
                    // 수정 api 호출

                    val nestedCommentModifyRequest = NestedCommentModifyRequest(
                        reply_id = if (item is NestedCommentResult) item.id else -1, // 여기서 수정
                        content = commentText
                    )

                    nestedCommentModifyAPI(nestedCommentModifyRequest, binding)

                    showTextView()
                }
            }

        }

        fun showEditText() {
            // TextView를 숨기고 EditText를 보여줌
            binding.nestedCommentContentTv.visibility = View.INVISIBLE
            binding.nestedCommentModifyEdit.visibility = View.VISIBLE
            binding.nestedCommentModifyEdit.setText(binding.nestedCommentContentTv.text)
            binding.nestedCommentModifyEdit.requestFocus() // EditText에 포커스를 주어 키보드가 나타나도록 함
            binding.nestedOption.visibility = View.GONE
            binding.check.visibility = View.VISIBLE
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

    fun nestedShowEditText(nestedCommentPosition: Int) {
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

    fun getNestedItem(position: Int): Any? {
        return if (position in 0 until items.size) {
            items[position]
        } else {
            null
        }
    }


    // 서버로 수정 데이터 전송하는 함수
    private fun nestedCommentModifyAPI(nestedCommentModifyRequest: NestedCommentModifyRequest, binding: ItemNestedCommentBinding) {
        // 서버 주소
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val nestedCommentModifyService = retrofit.create(NestedCommentModifyService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        nestedCommentModifyService.nestedCommentModify(serverToken, nestedCommentModifyRequest).enqueue(object :
            Callback<NestedCommentModifyResponse> {
            override fun onResponse(call: Call<NestedCommentModifyResponse>, response: Response<NestedCommentModifyResponse>) {
                if (response.isSuccessful) {
                    val nestedCommentModifyResponse = response.body()
                    val isSuccess = nestedCommentModifyResponse?.isSuccess
                    val code = nestedCommentModifyResponse?.code
                    val message = nestedCommentModifyResponse?.message
                    if (nestedCommentModifyResponse != null && nestedCommentModifyResponse.isSuccess) {
                        Log.d("NestedCommentAdapter - 답글 수정", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        // 수정 성공 시 토스트 메시지 표시
                        showToast("성공적으로 답글을 수정했습니다")

                        binding.check.visibility = View.GONE
                        binding.nestedOption.visibility = View.VISIBLE

                    } else {
                        // 응답 에러 코드 분류
                        nestedCommentModifyResponse?.let {
                            context.errorCodeList(
                                errorCode = it.code,
                                message = it.message,
                                type = "NestedComment",
                                detailType = "MODIFY",
                                intentData = null
                            )
                        }

                        // 수정 실패 시 토스트 메시지 표시
                        showToast("답글 수정에 실패했습니다")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("NestedCommentAdapter - 답글 수정", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<NestedCommentModifyResponse>, t: Throwable) {
                // 통신 실패
                Log.d("NestedCommentAdapter - 답글 수정", "통신 실패 - ${t.message}")
            }
        })
    }
    // 토스트 메시지 표시 함수 추가
    private fun showToast(message: String) {
        val toastBinding = LayoutToastBinding.inflate(LayoutInflater.from(context))
        toastBinding.toastMessage.text = message
        val toast = Toast(context)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

}
