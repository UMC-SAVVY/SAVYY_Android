package com.example.savvy_android.diary.adapter

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemCommentBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.activity.DiaryCommentActivity
import com.example.savvy_android.diary.data.comment.CommentModifyRequest
import com.example.savvy_android.diary.data.comment.CommentModifyResponse
import com.example.savvy_android.diary.data.comment.CommentRequest
import com.example.savvy_android.diary.data.comment.CommentResult
import com.example.savvy_android.diary.data.comment.NestedCommentRequest
import com.example.savvy_android.diary.data.comment.NestedCommentResponse
import com.example.savvy_android.diary.data.comment.NestedCommentResult
import com.example.savvy_android.diary.service.CommentDeleteService
import com.example.savvy_android.diary.service.CommentModifyService
import com.example.savvy_android.diary.service.NestedCommentDeleteService
import com.example.savvy_android.diary.service.NestedCommentService
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
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

    private val nestedCommentMap: MutableMap<Int, NestedCommentAdapter> = mutableMapOf()
    private val nestedCommentCounts: MutableList<Int> = mutableListOf()
    private lateinit var recyclerView: RecyclerView // RecyclerView 변수를 추가
    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의
    private val expandedItems: MutableMap<Int, Boolean> = mutableMapOf()

    // 아직 commentID 안 받아옴

    inner class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var nestedCommentAdapter: NestedCommentAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Any) {

//            val nestedComments: MutableList<NestedCommentRequest> = mutableListOf()

            nestedCommentAdapter = NestedCommentAdapter(context ,mutableListOf(), nestedOptionClickListener, adapterPosition)
            nestedCommentMap[adapterPosition] = nestedCommentAdapter
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
                    activity.refreshCommentList(activity.getDiaryId())

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


            //댓글 수정 입력 변화 이벤트 처리
            binding.commentModifyEdit.addTextChangedListener(object : TextWatcher {
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
                val commentText = binding.commentModifyEdit.text.toString()
                if (commentText.isNotEmpty()) {
                    // 버튼을 눌렀을 때 한 글자 이상 입력되었을 때
                    // 수정 api 호출

                    val commentModifyRequest = CommentModifyRequest(
                        comment_id = if (item is CommentResult) item.id else -1, // 여기서 수정
                        content = commentText
                    )

                    commentModifyAPI(commentModifyRequest, binding)

                    showTextView()

                }
            }

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
            if (position in 0 until nestedCommentCounts.size && nestedCommentCounts[position] > 0) {
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
            binding.option.visibility = View.GONE
            binding.check.visibility = View.VISIBLE

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



    fun removeCommentAtPosition(position: Int) {
        if (position in 0 until items.size) {
            val removedItem = items[position]
            items.removeAt(position)

            // nestedCommentCounts 리스트도 해당 인덱스 제거
            if (position in 0 until nestedCommentCounts.size) {
                nestedCommentCounts.removeAt(position)
            }

            notifyItemRemoved(position)

            // 만약 삭제된 아이템이 CommentResult 타입이면 삭제 API 호출
            if (removedItem is CommentResult) {
                val commentId = removedItem.id.toString()
                commentRemoveAPI(commentId = commentId)
                Log.d("CommentDelete", "commentId: $commentId, position: $position")
            }
        }
    }


    fun removeNestedComment(commentPosition: Int, nestedCommentPosition: Int) {
        val comment = items[commentPosition]
        if (comment is CommentResult) {
            val nestedComments: MutableList<NestedCommentResult> = comment.reply_List
            if (nestedCommentPosition in 0 until nestedComments.size) {
                val removedNestedComment = nestedComments.removeAt(nestedCommentPosition)
                notifyItemChanged(commentPosition)

                // 새로 추가한 코드. 안되면 삭제할 것
                // 삭제한 아이템을 notifyItemRemoved로 알려줌
                items[commentPosition] = comment
                notifyItemChanged(commentPosition)

                // 대댓글이 삭제되었으므로 댓글의 대댓글 개수를 감소시킴
                val viewHolder = getCommentViewHolder(commentPosition)
                viewHolder?.decrementCommentNum(commentPosition)

                // 삭제한 대댓글의 id를 가져옴
                val removedNestedCommentId = removedNestedComment.id.toString()

                // 대댓글 삭제 API 호출
                nestedCommentRemoveAPI(removedNestedCommentId)

                // commentNum이 아무리 해도 감소를 안해서 일단 대댓글 삭제하면 댓글 조회 api 호출
                // 대댓글 삭제 후 댓글 목록을 갱신하는 함수를 호출
                val activity = context as DiaryCommentActivity
                activity.refreshCommentList(activity.getDiaryId())
            }
        }
    }

    // Comment 수정하기 (text->editText)
    fun showCommentEditText(position: Int) {
        // 댓글 수정 시 해당 CommentViewHolder의 showEditText() 함수 호출
        val viewHolder = getCommentViewHolder(position)
        viewHolder?.showEditText()
    }


    // CommentAdapter 내의 함수
    fun showNestedCommentEditText(commentPosition: Int, nestedCommentPosition: Int) {
        val nestedCommentAdapter = nestedCommentMap[commentPosition]
        nestedCommentAdapter?.nestedShowEditText(nestedCommentPosition)
    }

    // NestedCommentAdapter의 아이템을 가져오기 위한 중개 함수
    fun getNestedCommentItem(commentPosition: Int, nestedCommentPosition: Int): Any? {
        val nestedCommentAdapter = nestedCommentMap[commentPosition]
        return nestedCommentAdapter?.getNestedItem(nestedCommentPosition)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    // 댓글 아이템을 position에 해당하는 뷰 홀더를 반환하는 함수
    fun getCommentViewHolder(position: Int): CommentViewHolder? {
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

    fun getItem(position: Int): Any? {
        return if (position in 0 until items.size) {
            items[position]
        } else {
            null
        }
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

    // 다이어리 댓글 삭제 API
    fun commentRemoveAPI(commentId: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val commentDeleteService = retrofit.create(CommentDeleteService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // Delete 요청
        commentDeleteService.commentDelete(
            token = accessToken,
            commentId = commentId
        )
            .enqueue(object : Callback<ServerDefaultResponse> {
                override fun onResponse(
                    call: Call<ServerDefaultResponse>,
                    response: Response<ServerDefaultResponse>
                ) {
                    if (response.isSuccessful) {
                        val deleteResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (deleteResponse?.isSuccess == true) {
                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("성공적으로 삭제가 완료되었습니다")
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                context.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "Comment",
                                    detailType = "DELETE",
                                    intentData = null
                                )
                            }

                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("댓글 삭제를 실패했습니다")
                        }
                    } else {
                        Log.e(
                            "Comment",
                            "[COMMENT DELETE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )

                        // 삭제 성공 시 토스트 메시지 표시
                        showToast("댓글 삭제를 실패했습니다")
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("Comment", "[COMMENT DELETE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                    // 삭제 성공 시 토스트 메시지 표시
                    showToast("댓글 삭제를 실패했습니다")
                }
            })
    }

    // 서버로 수정 데이터 전송하는 함수
    private fun commentModifyAPI(commentModifyRequest: CommentModifyRequest, binding: ItemCommentBinding) {
        // 서버 주소
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val commentModifyService = retrofit.create(CommentModifyService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        commentModifyService.commentModify(serverToken, commentModifyRequest).enqueue(object :
            Callback<CommentModifyResponse> {
            override fun onResponse(call: Call<CommentModifyResponse>, response: Response<CommentModifyResponse>) {
                if (response.isSuccessful) {
                    val commentModifyResponse = response.body()
                    val isSuccess = commentModifyResponse?.isSuccess
                    val code = commentModifyResponse?.code
                    val message = commentModifyResponse?.message
                    if (commentModifyResponse != null && commentModifyResponse.isSuccess) {
                        Log.d("DiaryCommentAdapter - 댓글 수정", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        // 수정 성공 시 토스트 메시지 표시
                        showToast("성공적으로 댓글을 수정했습니다")

                        binding.check.visibility = View.GONE
                        binding.option.visibility = View.VISIBLE

                    } else {
                        // 응답 에러 코드 분류
                        commentModifyResponse?.let {
                            context.errorCodeList(
                                errorCode = it.code,
                                message = it.message,
                                type = "Comment",
                                detailType = "MODIFY",
                                intentData = null
                            )
                        }

                        // 수정 실패 시 토스트 메시지 표시
                        showToast("댓글 수정에 실패했습니다")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("DiaryCommentAdapter - 댓글 수정", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<CommentModifyResponse>, t: Throwable) {
                // 통신 실패
                Log.d("DiaryCommentAdapter - 댓글 수정", "통신 실패 - ${t.message}")
            }
        })
    }

    fun nestedCommentRemoveAPI(replyId: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val nestedCommentDeleteService = retrofit.create(NestedCommentDeleteService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // Delete 요청
        nestedCommentDeleteService.nestedCommentDelete(
            token = accessToken,
            replyId = replyId
        )
            .enqueue(object : Callback<ServerDefaultResponse> {
                override fun onResponse(
                    call: Call<ServerDefaultResponse>,
                    response: Response<ServerDefaultResponse>
                ) {
                    if (response.isSuccessful) {
                        val deleteResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (deleteResponse?.isSuccess == true) {
                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("성공적으로 삭제가 완료되었습니다")
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                context.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "NestedComment",
                                    detailType = "DELETE",
                                    intentData = null
                                )
                            }

                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("답글 삭제를 실패했습니다")
                        }
                    } else {
                        Log.e(
                            "NESTED COMMENT",
                            "[NESTED COMMENT DELETE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )

                        // 삭제 성공 시 토스트 메시지 표시
                        showToast("답글 삭제를 실패했습니다")
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("NESTED COMMENT", "[NESTED COMMENT DELETE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                    // 삭제 성공 시 토스트 메시지 표시
                    showToast("답글 삭제를 실패했습니다")
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
