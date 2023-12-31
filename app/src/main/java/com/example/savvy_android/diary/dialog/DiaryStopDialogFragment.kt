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
import com.example.savvy_android.init.MainActivity
import com.example.savvy_android.databinding.DialogDiaryStopBinding

class DiaryStopDialogFragment(private val isDiary: Boolean) : DialogFragment() {
    private var _binding: DialogDiaryStopBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogDiaryStopBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(true)


        binding.btnStop.setOnClickListener {
            dismiss()

            // MainActivity로 이동하면서 실행된 Fragment로 이동
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if(isDiary)
                intent.putExtra("SHOW_DIARY_FRAGMENT", true) // DiaryFragment를 보여주도록 추가 데이터 전달
            else
                intent.putExtra("SHOW_HOME_FRAGMENT", true) // HomeFragment를 보여주도록 추가 데이터 전달
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