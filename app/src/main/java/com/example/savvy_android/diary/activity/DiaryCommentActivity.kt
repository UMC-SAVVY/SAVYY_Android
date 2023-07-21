package com.example.savvy_android.diary.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityDiaryCommentBinding
import com.example.savvy_android.diary.adapter.CommentAdapter
import com.example.savvy_android.diary.dialog.CommentDeleteDialogFragment
import com.example.savvy_android.diary.dialog.CommentModifyDialogFragment
import com.example.savvy_android.diary.dialog.DiaryModifyDialogFragment
import com.example.savvy_android.plan.adapter.MakeDateAddAdapter
import com.example.savvy_android.utils.BottomSheetDialogFragment

class DiaryCommentActivity : AppCompatActivity(), CommentAdapter.OnOptionClickListener {

    private lateinit var binding: ActivityDiaryCommentBinding
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 뒤로가기 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // comment_btn 클릭 시 새로운 댓글 추가
        binding.commentBtn.setOnClickListener {
            val newComment = binding.commentEdit.text.toString()
            if (newComment.isNotBlank()) {
                commentAdapter.addItem(newComment)
                binding.commentEdit.text.clear() // 댓글 입력창 비우기
            }
        }

        // RecyclerView에 CommentAdapter 설정
        commentAdapter = CommentAdapter(mutableListOf(), this)
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
        val bottomSheet = BottomSheetDialogFragment()
        bottomSheet.setButtonClickListener(object : BottomSheetDialogFragment.OnButtonClickListener {

            // 수정하기
            override fun onDialogEditClicked() {
                val dialog = CommentModifyDialogFragment()
                dialog.show(supportFragmentManager, "CommentModifyDialog")
            }

            // 삭제하기
            override fun onDialogDeleteClicked() {
                val dialog = CommentDeleteDialogFragment()
                dialog.show(supportFragmentManager, "CommentDeleteDialog")

            }
        })
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val buttonColor = if (able) {
            ContextCompat.getColor(button.context, R.color.main)
        } else {
            ContextCompat.getColor(button.context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }

}