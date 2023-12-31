package com.example.savvy_android.diary.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.databinding.DialogPlanModifyBinding
import com.example.savvy_android.databinding.LayoutToastBinding

class ModifySaveDialogFragment : DialogFragment() {
    private var _binding: DialogPlanModifyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogPlanModifyBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(true)


        binding.btnSave.setOnClickListener {
            buttonClickListener.onDialogBtnOClicked()

            dismiss()

//            // 커스텀 Toast 메시지 생성
//            val toastBinding = LayoutToastBinding.inflate(layoutInflater)
//            toastBinding.toastMessage.text = "성공적으로 수정이 완료되었습니다"
//
//            val toast = Toast(requireContext())
//            toast.duration = Toast.LENGTH_SHORT
//            toast.view = toastBinding.root
//
//            toast.setGravity(Gravity.TOP, 0, 120)  //toast 위치 설정
//
//            toast.show()

            val intent = Intent(activity, PlanDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


        binding.cancelBtn.setOnClickListener {
            buttonClickListener.onDialogBtnXClicked()

            dismiss()
        }
        return view
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

    interface OnButtonClickListener {
        fun onDialogBtnOClicked()
        fun onDialogBtnXClicked()
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener

}