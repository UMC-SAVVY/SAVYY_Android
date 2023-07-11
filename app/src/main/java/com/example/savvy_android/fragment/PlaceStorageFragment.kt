package com.example.savvy_android.fragment

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
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.adapter.PlaceStorageAdapter
import com.example.savvy_android.data.PlaceStorageItemData
import com.example.savvy_android.databinding.FragmentPlaceStorageBinding

class PlaceStorageFragment : Fragment() {
    private lateinit var binding: FragmentPlaceStorageBinding
    private lateinit var placeStorageAdapter: PlaceStorageAdapter
    private var planStorageData = arrayListOf<PlaceStorageItemData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceStorageBinding.inflate(inflater, container, false)
        return binding.root
    }


    // 닉네임 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeSearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.placeSearchEdit.length() != 0
                binding.storageSearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.storageSearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // SearchNewPlace Data & Adapter
        placeStorageAdapter = PlaceStorageAdapter(planStorageData)
        binding.storageSearchRecycle.adapter = placeStorageAdapter
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