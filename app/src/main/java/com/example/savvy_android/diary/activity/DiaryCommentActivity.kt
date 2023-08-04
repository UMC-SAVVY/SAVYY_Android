package com.example.savvy_android.diary.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityDiaryCommentBinding
import com.example.savvy_android.databinding.ActivityPlanDetialBinding
import com.example.savvy_android.diary.adapter.CommentAdapter
import com.example.savvy_android.diary.adapter.NestedCommentAdapter
import com.example.savvy_android.diary.data.CommentItemData
import com.example.savvy_android.diary.data.comment.CommentCheckResponse
import com.example.savvy_android.diary.dialog.CommentDeleteDialogFragment
import com.example.savvy_android.diary.dialog.CommentModifyDialogFragment
import com.example.savvy_android.diary.service.CommentCheckService
import com.example.savvy_android.plan.data.PlanDetailResponse
import com.example.savvy_android.plan.service.PlanDetailService
import com.example.savvy_android.utils.BottomSheetDialogFragment
import com.example.savvy_android.utils.BottomSheetOtherDialogFragment
import com.example.savvy_android.utils.memo.MemoActivity
import com.example.savvy_android.utils.report.ReportActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiaryCommentActivity : AppCompatActivity(),
    CommentAdapter.OnOptionClickListener,
    NestedCommentAdapter.OnNestedOptionClickListener {

    private lateinit var binding: ActivityDiaryCommentBinding
    private lateinit var commentAdapter: CommentAdapter
    private var diaryID: Int = 0
    private var isMine: Boolean = true // 댓글이 본인것인지 판단
    private lateinit var nickname: String
    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)
        nickname = sharedPreferences.getString("USER_NICKNAME", null)!!

        diaryID = intent.getIntExtra("diaryID", 0)

        commentCheckAPI(diaryID)


        // 뒤로가기 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // comment_btn 클릭 시 새로운 댓글 추가
        binding.commentBtn.setOnClickListener {
            val newCommentContent = binding.commentEdit.text.toString()
            if (newCommentContent.isNotBlank()) {
                // 댓글 정보를 담은 CommentItemData 객체 생성
                val newCommentItem = CommentItemData(
                    position = 0,
                    userName = "내가 쓴 댓글",
                    commentContent = newCommentContent,
                    date = "",
                    commentNum = 0,
                    nestedComment = mutableListOf()
                )

                commentAdapter.addItem(newCommentItem) // 댓글 추가
                binding.commentEdit.text.clear() // 댓글 입력창 비우기
            }
        }

        // RecyclerView에 CommentAdapter 설정
        commentAdapter = CommentAdapter(mutableListOf(), this, this)
        binding.recyclerviewComment.adapter = commentAdapter
        binding.recyclerviewComment.layoutManager = LinearLayoutManager(this)


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

    // OnOptionClickListener 인터페이스의 onOptionClick 메서드 구현
    override fun onOptionClick(position: Int) {
        if(isMine){
            val bottomSheet = BottomSheetDialogFragment()
            bottomSheet.setButtonClickListener(object : BottomSheetDialogFragment.OnButtonClickListener {

                // 수정하기
                override fun onDialogEditClicked() {
                    val dialog = CommentModifyDialogFragment()
                    dialog.setButtonClickListener(object : CommentModifyDialogFragment.OnButtonClickListener {

                        // 수정하기 버튼 클릭 시
                        override fun onDialogModifyBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                            commentAdapter.showCommentEditText(position)

                        }

                        // 취소하기 버튼 클릭 시
                        override fun onDialogCancelBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                        }
                    })
                    dialog.show(supportFragmentManager, "CommentModifyDialog")
                }

                // 삭제하기
                override fun onDialogDeleteClicked() {
                    val dialog = CommentDeleteDialogFragment()
                    dialog.setButtonClickListener(object : CommentDeleteDialogFragment.OnButtonClickListener {

                        // 삭제하기 버튼 클릭 시
                        override fun onDialogDeleteBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                            commentAdapter.removeCommentAtPosition(position)
                        }

                        // 취소하기 버튼 클릭 시
                        override fun onDialogCancelBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                        }
                    })
                    dialog.show(supportFragmentManager, "CommentModifyDialog")
                }
            })
            bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")
        }

        else{
            // 옵션 관련 (다른사람이 작성한 댓글)
            val bottomSheetOther = BottomSheetOtherDialogFragment()
            bottomSheetOther.setButtonClickListener(object :
                BottomSheetOtherDialogFragment.OnButtonClickListener {
                override fun onDialogReportClicked() {
                    val intent = Intent(this@DiaryCommentActivity, ReportActivity::class.java)
                    startActivity(intent)
                }
            })
            bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")
        }

    }

    override fun onNestedOptionClick(commentPosition: Int, nestedCommentPosition: Int) {
        val commentAdapter = binding.recyclerviewComment.adapter as CommentAdapter

        if(isMine){
            val bottomSheet = BottomSheetDialogFragment()
            bottomSheet.setButtonClickListener(object : BottomSheetDialogFragment.OnButtonClickListener {
                // 수정하기
                override fun onDialogEditClicked() {
                    val dialog = CommentModifyDialogFragment()
                    dialog.setButtonClickListener(object : CommentModifyDialogFragment.OnButtonClickListener {

                        // 수정하기 버튼 클릭 시
                        override fun onDialogModifyBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                            commentAdapter.showNestedCommentEditText(commentPosition, nestedCommentPosition)
                        }

                        // 취소하기 버튼 클릭 시
                        override fun onDialogCancelBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                        }
                    })
                    dialog.show(supportFragmentManager, "CommentModifyDialog")
                }

                // 삭제하기
                override fun onDialogDeleteClicked() {
                    val dialog = CommentDeleteDialogFragment()
                    dialog.setButtonClickListener(object : CommentDeleteDialogFragment.OnButtonClickListener {
                        // 삭제하기 버튼 클릭 시
                        override fun onDialogDeleteBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                            commentAdapter.removeNestedComment(commentPosition, nestedCommentPosition)
                        }

                        // 취소하기 버튼 클릭 시
                        override fun onDialogCancelBtnClicked() {
                            dialog.dismiss() // 다이얼로그 닫기
                        }
                    })
                    dialog.show(supportFragmentManager, "CommentModifyDialog")
                }
            })
            bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")
        }
        else{
            // 옵션 관련 (다른사람이 작성한 댓글)
            val bottomSheetOther = BottomSheetOtherDialogFragment()
            bottomSheetOther.setButtonClickListener(object :
                BottomSheetOtherDialogFragment.OnButtonClickListener {
                override fun onDialogReportClicked() {
                    val intent = Intent(this@DiaryCommentActivity, ReportActivity::class.java)
                    startActivity(intent)
                }
            })
            bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")
        }

    }

    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val buttonColor = if (able) {
            ContextCompat.getColor(button.context, R.color.main)
        } else {
            ContextCompat.getColor(button.context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }


    private fun commentCheckAPI(diaryId: Int) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val commentCheckService = retrofit.create(CommentCheckService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!
        commentCheckService.commentCheck(serverToken, diaryId.toString()).enqueue(object :
            Callback<CommentCheckResponse> {
            override fun onResponse(call: Call<CommentCheckResponse>, response: Response<CommentCheckResponse>) {
                if (response.isSuccessful) {
                    val commentCheckResponse = response.body()
                    val isSuccess = commentCheckResponse?.isSuccess
                    val code = commentCheckResponse?.code
                    val message = commentCheckResponse?.message
                    if (commentCheckResponse != null && commentCheckResponse.isSuccess) {
                        val commentCheckResult = commentCheckResponse.result
                        // planDetailResult에 들어있는 데이터를 사용하여 작업
                        Log.d("DiaryCommentActivity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")
//                        val firstComment = commentCheckResult[0]
//
//                        isMine = nickname == firstComment.nickname
//
//                        diaryID = firstComment.id

                    } else {
                        Log.d("DiaryCommentActivity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                    }
                } else {
                    val errorCode = response.code()
                    Log.e("DiaryCommentActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<CommentCheckResponse>, t: Throwable) {
                // 통신 실패
                Log.e("DiaryCommentActivity", "통신 실패 - ${t.message}")
            }
        })
    }


}