package com.example.savvy_android.myPage.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.databinding.DialogMypageWithdrawalBinding
import com.example.savvy_android.init.SplashActivity


class MypageWithdrawalDialogFragment : DialogFragment() {
    private var _binding: DialogMypageWithdrawalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogMypageWithdrawalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다이얼로그 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        // 탈퇴 버튼 클릭 시
        binding.dialogWithdrawalBtnO.setOnClickListener {
//            buttonClickListener.onDialogPlanBtnOClicked()
            dismiss()

            // 초기 화면으로 이동
            val intent = Intent(requireContext(), SplashActivity::class.java)
            // 이전에 존재하던 모든 acitivty 종료
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 취소하기 버튼 클릭 시
        binding.dialogWithdrawalBtnX.setOnClickListener {
//            buttonClickListener.onDialogPlanBtnXClicked()
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

    // 인터페이스
    interface OnButtonClickListener {
        fun onDialogPlanBtnOClicked()
        fun onDialogPlanBtnXClicked()
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}
