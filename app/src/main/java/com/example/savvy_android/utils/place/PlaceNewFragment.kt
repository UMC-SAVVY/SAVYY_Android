package com.example.savvy_android.utils.place

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.utils.place.PlaceNewAdapter
import com.example.savvy_android.utils.place.PlaceNewItemData
import com.example.savvy_android.databinding.FragmentPlaceNewBinding

class PlaceNewFragment : Fragment() {
    private lateinit var binding: FragmentPlaceNewBinding
    private lateinit var placeNewAdapter: PlaceNewAdapter
    private var planStorageData = arrayListOf<PlaceNewItemData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlaceNewBinding.inflate(inflater, container, false)
        return binding.root

    }

    // 닉네임 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaceSearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.newPlaceSearchEdit.length() != 0
                binding.newPlaceSearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.newPlaceSearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 검색 버튼 클릭 이벤트
        binding.newPlaceSearchBtn.setOnClickListener {
            // 안내 문구가 보이고 이는 경우, 안 보이도록 설정
            if (binding.searchPlaceNotice.isVisible)
                binding.searchPlaceNotice.isVisible = false
        }

        // SearchNewPlace Data & Adapter
        placeNewAdapter = PlaceNewAdapter(planStorageData)
        binding.searchNewRecycle.adapter = placeNewAdapter
    }

    // 클릭 가능 여부에 따른 button 배경 변경 함수
    private fun btnStateBackground(able: Boolean, button: AppCompatButton) {
        val context: Context = requireContext()
        val buttonColor = if (able) {
            ContextCompat.getColor(context, R.color.main)
        } else {
            ContextCompat.getColor(context, R.color.button_line)
        }
        button.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }
}