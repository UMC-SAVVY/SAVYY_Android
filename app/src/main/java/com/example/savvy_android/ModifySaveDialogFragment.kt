package com.example.savvy_android

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.savvy_android.databinding.FragmentModifySaveDialogBinding
import com.example.savvy_android.databinding.LayoutToastBinding

class ModifySaveDialogFragment : DialogFragment() {
    private var _binding: FragmentModifySaveDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentModifySaveDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.setCancelable(true)


        binding.icX.setOnClickListener {
            dismiss()    // 대화상자를 닫는 함수
        }
        binding.saveBtn.setOnClickListener {
            dismiss()

            // 커스텀 Toast 메시지 생성
            val toastBinding = LayoutToastBinding.inflate(layoutInflater)
            toastBinding.toastMessage.text = "성공적으로 수정이 완료되었습니다"

            val toast = Toast(requireContext())
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastBinding.root

            toast.setGravity(Gravity.TOP, 0, 120)  //toast 위치 설정

            toast.show()

            val intent = Intent(activity, TravelPlanViewActivity::class.java)
            startActivity(intent)
        }


        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        return view
    }


    override fun onResume() {
        super.onResume()

        //dialog의 크기가 지정한 대로 나오지 않아서 작성된 코드
        // 기기의 디스플레이 크기를 가져옴
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val deviceWidth = size.x

        // 대화 상자의 너비를 기기 너비의 90%로 설정
        val params = dialog?.window?.attributes
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}