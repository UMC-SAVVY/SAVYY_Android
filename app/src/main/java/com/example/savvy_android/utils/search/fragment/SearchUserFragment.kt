package com.example.savvy_android.utils.search.fragment

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
import com.example.savvy_android.databinding.FragmentSearchUserBinding
import com.example.savvy_android.utils.search.adapter.SearchUserAdapter
import com.example.savvy_android.utils.search.data.SearchUserItemData

class SearchUserFragment : Fragment() {
    private lateinit var binding: FragmentSearchUserBinding
    private lateinit var recordAdapter: SearchUserAdapter
    private var recordData = arrayListOf<SearchUserItemData>()
    private lateinit var searchAdapter: SearchUserAdapter
    private var searchData = arrayListOf<SearchUserItemData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchUserBinding.inflate(inflater, container, false)

        // 닉네임 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.searchUserEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.searchUserEdit.length() != 0
                binding.searchUserBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.searchUserBtn)

                if (!isEnableState) {
                    binding.searchNoticeUser.visibility = View.VISIBLE
                    binding.searchRecordUserRecycle.visibility = View.VISIBLE
                    binding.searchResultUserRecycle.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 검색 버튼 클릭 이벤트
        binding.searchUserBtn.setOnClickListener {
            // 안내 문구 안 보이도록 설정
            binding.searchNoticeUser.visibility = View.GONE
            binding.searchRecordUserRecycle.visibility = View.GONE
            binding.searchResultUserRecycle.visibility = View.VISIBLE
            Log.e("TEST", "유저 검색")
        }

        // 검색 기록 삭제하기
        binding.searchUserClear.setOnClickListener {
            // 삭제 API를 넣어야함~~~
        }

        // 검색 기록에 대한 Data & Adapter
        recordAdapter = SearchUserAdapter(recordData)
        binding.searchRecordUserRecycle.adapter = recordAdapter

        // 검색 결과에 대한 Data & Adapter
        searchAdapter = SearchUserAdapter(searchData)
        binding.searchResultUserRecycle.adapter = searchAdapter

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