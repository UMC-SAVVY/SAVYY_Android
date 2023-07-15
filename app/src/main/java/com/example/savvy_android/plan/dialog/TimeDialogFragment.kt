package com.example.savvy_android.plan.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.databinding.DialogTimePickerBinding

class TimeDialogFragment : DialogFragment() {
    private var _binding: DialogTimePickerBinding? = null
    private val binding get() = _binding!!

    private var onTimeSelectedListener: ((Int, Int, Int, Int) -> Unit)? = null

    private var hour1: Int = 0
    private var minute1: Int = 0
    private var hour2: Int = 0
    private var minute2: Int = 0

    fun setOnTimeSelectedListener(listener: (Int, Int, Int, Int) -> Unit) {
        onTimeSelectedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogTimePickerBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(true)


        binding.btnSave.setOnClickListener {
            val selectedHour1 = binding.TimePicker1.hour
            val selectedMinute1 = binding.TimePicker1.minute
            val selectedHour2 = binding.TimePicker2.hour
            val selectedMinute2 = binding.TimePicker2.minute

            onTimeSelectedListener?.invoke(selectedHour1, selectedMinute1, selectedHour2, selectedMinute2)

            dismiss()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTimePicker()
    }

    private fun setupTimePicker() {
        binding.TimePicker1.setIs24HourView(true)
        binding.TimePicker1.hour = hour1
        binding.TimePicker1.minute = minute1

        binding.TimePicker2.setIs24HourView(true)
        binding.TimePicker2.hour = hour2
        binding.TimePicker2.minute = minute2
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}