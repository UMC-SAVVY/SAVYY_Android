package com.example.savvy_android.diary.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.diary.activity.DiaryMake2Activity
import com.example.savvy_android.databinding.DialogPlanSelectBinding

class PlanSelectDialogFragment(
    private val isDiary: Boolean,
    private val checkedPlanID: Int,
) : DialogFragment() {
    private var _binding: DialogPlanSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogPlanSelectBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(true)


        // make2에서 받아서
        binding.btnSelect.setOnClickListener {
            dismiss()

            val intent = Intent(activity, DiaryMake2Activity::class.java)
            intent.putExtra("showDateAddItem", true)
            intent.putExtra("isDiary", isDiary)
            intent.putExtra("planID", checkedPlanID)
            startActivity(intent)
        }


        binding.cancelBtn.setOnClickListener {
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
}