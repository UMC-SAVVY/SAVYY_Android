package com.example.savvy_android.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.databinding.LayoutBottomSheetBinding


class BottomSheetDialogFragment : DialogFragment() {
    private var _binding: LayoutBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutBottomSheetBinding.inflate(inflater, container, false)
        val view = binding.root

        // 다이얼로그 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.setCancelable(true) //회색 배경 눌렀을 때 dialog 취소 여부

        // 수정하기 버튼 클릭 시
        binding.editBtn.setOnClickListener {
            buttonClickListener.onDialogEditClicked()
            dismiss()
        }

        // 삭제하기 버튼 클릭 시
        binding.deleteBinBtn.setOnClickListener {
            buttonClickListener.onDialogDeleteClicked()
            dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 너비를 match_parent로 설정
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.BOTTOM) //다이얼로그 하단
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 인터페이스
    interface OnButtonClickListener {
        fun onDialogEditClicked()
        fun onDialogDeleteClicked()
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}
