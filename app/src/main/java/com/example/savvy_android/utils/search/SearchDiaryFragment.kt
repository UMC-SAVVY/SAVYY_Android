package com.example.savvy_android.utils.search

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.savvy_android.R
import com.example.savvy_android.databinding.FragmentSearchDiaryBinding
import com.example.savvy_android.home.adapter.HomeAdapter
import com.example.savvy_android.home.adapter.HomeItemData

class SearchDiaryFragment : Fragment() {
    private lateinit var binding: FragmentSearchDiaryBinding
    private lateinit var recordAdapter: SearchRecordDiaryAdapter
    private var recordData = arrayListOf<SearchRecordDiaryItemData>()
    private lateinit var searchAdapter: HomeAdapter
    private var searchData = arrayListOf<HomeItemData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchDiaryBinding.inflate(inflater, container, false)

        // 닉네임 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.searchDiaryEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.searchDiaryEdit.length() != 0
                binding.searchDiaryBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.searchDiaryBtn)

                if (!isEnableState) {
                    binding.searchNoticeDiary.visibility = View.VISIBLE
                    binding.searchRecordDiaryRecycle.visibility = View.VISIBLE
                    binding.searchResultDiaryRecycle.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 검색 버튼 클릭 이벤트
        binding.searchDiaryBtn.setOnClickListener {
            // 안내 문구 안 보이도록 설정
            binding.searchNoticeDiary.visibility = View.GONE
            binding.searchRecordDiaryRecycle.visibility = View.GONE
            binding.searchResultDiaryRecycle.visibility = View.VISIBLE
        }

        // 검색 기록 삭제하기
        binding.searchWordClear.setOnClickListener {
            // 삭제 API를 넣어야함~~~
        }

        // 검색 기록에 대한 Data & Adapter
        recordAdapter = SearchRecordDiaryAdapter(recordData)
        binding.searchRecordDiaryRecycle.adapter = recordAdapter

        // 검색 결과에 대한 Data & Adapter
        searchAdapter = HomeAdapter(searchData)
        binding.searchResultDiaryRecycle.adapter = searchAdapter

        return binding.root
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