package com.example.savvy_android.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.databinding.DialogDatePickerBinding
import java.util.Calendar

class DateDialogFragment : DialogFragment() {
    private var _binding: DialogDatePickerBinding? = null
    private val binding get() = _binding!!

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0


    private var onDateSelectedListener: ((year: Int, month: Int, day: Int) -> Unit)? = null

    fun setOnDateSelectedListener(listener: (year: Int, month: Int, day: Int) -> Unit) {
        onDateSelectedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogDatePickerBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(true)


        // 기존에 선택된 날짜 가져와서 설정
        val cal = Calendar.getInstance()
        if (selectedYear == 0 && selectedMonth == 0 && selectedDay == 0) {
            // 선택된 날짜가 없으면 오늘 날짜로 초기화
            selectedYear = cal.get(Calendar.YEAR)
            selectedMonth = cal.get(Calendar.MONTH)
            selectedDay = cal.get(Calendar.DAY_OF_MONTH)
        }
        binding.CalenderView.date = cal.timeInMillis

        binding.CalenderView.setOnDateChangeListener { _, year, month, day ->
            selectedYear = year
            selectedMonth = month
            selectedDay = day

        }

        binding.btnSave.setOnClickListener {
            onDateSelectedListener?.invoke(selectedYear, selectedMonth, selectedDay)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}