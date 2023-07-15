package com.example.savvy_android.plan.dialog

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

    private var currentDate: Calendar? = null // 현재 설정된 날짜

    private var onDateSelectedListener: ((year: Int, month: Int, day: Int) -> Unit)? = null

    //날짜 선택 리스너를 저장 변수
    fun setOnDateSelectedListener(listener: (year: Int, month: Int, day: Int) -> Unit) {
        onDateSelectedListener = listener
    }

    //현재 설정된 날짜를 저장하는 변수
    fun setCurrentDate(date: Calendar?) {
        currentDate = date?.clone() as Calendar? //date를 통해 전달받은 Calendar 객체를 currentDate에 복사
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

        if (currentDate == null) {
            // 현재 설정된 날짜가 없는 경우
            currentDate = Calendar.getInstance()  // 오늘 날짜로 초기화
        }

        // CalendarView의 날짜를 현재 설정된 날짜로 설정
        binding.CalenderView.date = currentDate!!.timeInMillis

        // CalendarView의 날짜 변경 리스너 설정
        // 날짜가 변경될 때마다 currentDate를 업데이트
        binding.CalenderView.setOnDateChangeListener { _, year, month, day ->
            currentDate!!.set(year, month, day)
        }

        // 저장 버튼 클릭 시 선택된 날짜를 리스너를 통해 전달
        binding.btnSave.setOnClickListener {
            onDateSelectedListener?.invoke(
                currentDate!!.get(Calendar.YEAR),
                currentDate!!.get(Calendar.MONTH),
                currentDate!!.get(Calendar.DAY_OF_MONTH)
            )
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
