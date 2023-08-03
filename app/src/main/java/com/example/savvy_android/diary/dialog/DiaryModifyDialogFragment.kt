package com.example.savvy_android.diary.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.databinding.DialogDiaryModifyBinding
import com.example.savvy_android.diary.activity.DiaryModify1Activity
import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryHashtag

class DiaryModifyDialogFragment(
    private var diaryID: Int,
    private var title: String,
    private var diaryDetailData: ArrayList<DiaryContent>,
    private var diaryHashtagData: ArrayList<DiaryHashtag>,
) : DialogFragment() {
    private var _binding: DialogDiaryModifyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogDiaryModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다이얼로그 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        // 수정하기 버튼 클릭 시
        binding.btnModify.setOnClickListener {
            val intent = Intent(activity, DiaryModify1Activity::class.java)
            intent.putExtra("diaryID", diaryID)
            intent.putExtra("title", title)
            intent.putParcelableArrayListExtra("diaryContent", diaryDetailData)
            intent.putParcelableArrayListExtra("diaryHashtag", diaryHashtagData)
            startActivity(intent)
            dismiss()
        }

        // 취소하기 버튼 클릭 시
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 너비를 match_parent로 설정
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
