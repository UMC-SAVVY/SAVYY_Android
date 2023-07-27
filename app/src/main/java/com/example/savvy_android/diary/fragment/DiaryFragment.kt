package com.example.savvy_android.diary.fragment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.savvy_android.R
import com.example.savvy_android.diary.activity.DiaryMake1Activity
import com.example.savvy_android.diary.adapter.DiaryListAdapter
import com.example.savvy_android.diary.data.DiaryItemData
import com.example.savvy_android.databinding.FragmentDiaryBinding
import com.example.savvy_android.diary.DiaryItemTouchCallback
import com.example.savvy_android.utils.alarm.AlarmActivity

class DiaryFragment : Fragment() {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var diaryListAdapter: DiaryListAdapter
    private var diaryListData = arrayListOf<DiaryItemData>()
    private val diaryTouchSimpleCallback = DiaryItemTouchCallback()
    private val itemTouchHelper = ItemTouchHelper(diaryTouchSimpleCallback)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        // 알람 버튼 클릭시 알람 페이지 연결
        binding.diaryAlarm.setOnClickListener {
            val intent = Intent(context, AlarmActivity::class.java)
            startActivity(intent)
        }

        // 검색 EditText 입력 변화 이벤트 처리 (한글자라도 입력 시)
        binding.diarySearchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEnableState = binding.diarySearchEdit.length() != 0
                binding.diarySearchBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.diarySearchBtn)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Plan Data & Adapter
        diaryListAdapter =
            DiaryListAdapter(
                binding.diaryRecycle,
                diaryListData,
                requireActivity().supportFragmentManager,
                true
            )
        binding.diaryRecycle.adapter = diaryListAdapter


        // itemTouchHelper와 recyclerview 연결
        itemTouchHelper.attachToRecyclerView(binding.diaryRecycle)

        // Floating Button 클릭 시 계획서 작성 페이지로 연결
        binding.diaryAddFbtn.setOnClickListener {
            val intent = Intent(context, DiaryMake1Activity::class.java)
            intent.putExtra("isDiary",true)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // 알람 존재 여부에 따른 알람 버튼 형태
        val hasAlarm = true
        if (hasAlarm)
            binding.diaryAlarm.setImageResource(R.drawable.ic_alarm_o)
        else
            binding.diaryAlarm.setImageResource(R.drawable.ic_alarm_x)

        // 검색 기능
        binding.diarySearchBtn.setOnClickListener {
            Log.e("TEST", "검색 버튼 눌림")
            searchDiaryList(binding.diarySearchEdit.text.toString())
        }
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

    // 목록 검색 API
    private fun searchDiaryList(searchText: String) {
        Log.e("TEST", "$searchText")
        // 검색 단어를 포함하는지 확인
        // 검색 API
    }
}