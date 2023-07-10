package com.example.savvy_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemDateAddBinding
import com.example.savvy_android.dialog.DateDialogFragment
import java.util.Calendar

class DateAddAdapter(private val data: MutableList<String>, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<DateAddAdapter.ViewHolder>() {
    private val placeAddMap: MutableMap<Int, PlaceAddAdapter> = mutableMapOf()

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDateAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, position)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: ItemDateAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var placeAddAdapter: PlaceAddAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String, position: Int) {

            // placeAdd RecyclerView 설정
            placeAddAdapter = PlaceAddAdapter(mutableListOf(""), fragmentManager)
            placeAddMap[position] = placeAddAdapter
            binding.recyclerviewPlaceAdd.adapter = placeAddAdapter
            binding.recyclerviewPlaceAdd.layoutManager = LinearLayoutManager(itemView.context)

            // add_place_btn 클릭 시 새로운 장소 추가
            binding.addPlaceBtn.setOnClickListener {
                val newItem = ""
                placeAddAdapter.addItem(newItem)
            }

            //오늘 날짜와 추가된 item의 날짜 표시
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1 // 현재 월 가져오기 (월은 0부터 시작하므로 +1)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val currentDate = cal.clone() as Calendar // 현재 날짜 복사
            currentDate.set(year, month - 1, day) // 복사된 날짜를 현재 날짜로 설정

            val newDate = currentDate.clone() as Calendar // 새로운 날짜 생성
            newDate.add(Calendar.DAY_OF_MONTH, position) // 새로운 날짜를 기존 날짜의 하루 뒤로 설정

            val newYear = newDate.get(Calendar.YEAR)
            val newMonth = newDate.get(Calendar.MONTH) + 1
            val newDay = newDate.get(Calendar.DAY_OF_MONTH)

            binding.travelDateTv.text = "$newYear.$newMonth.$newDay" // 텍스트뷰에 날짜 표시

            // 날짜 설정 버튼 클릭 시 DateDialogFragment 띄우기
            binding.dateSettingBtn.setOnClickListener {
                val dateDialogFragment = DateDialogFragment()
                dateDialogFragment.setOnDateSelectedListener { year, month, day ->
                    val formattedMonth = month + 1 // 선택된 월 값 보정 (월은 0부터 시작하므로 +1)
                    val selectedDate = "${year}.$formattedMonth.$day"
                    binding.travelDateTv.text = selectedDate
                }
                dateDialogFragment.show(fragmentManager, "DateDialog")
            }
        }
    }

    // DateAdd 추가
    fun addItem(item: String) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }
}


