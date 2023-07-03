package com.example.savvy_android

import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemDateAddBinding
import java.util.Calendar

class DateAddAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<DateAddAdapter.ViewHolder>() {
    private lateinit var placeAddAdapter: PlaceAddAdapter

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateAddAdapter.ViewHolder {
        val binding =
            ItemDateAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: DateAddAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: ItemDateAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String) {

            // placeAdd RecyclerView 설정
            placeAddAdapter = PlaceAddAdapter(mutableListOf(""))
            binding.recyclerviewPlaceAdd.adapter = placeAddAdapter
            binding.recyclerviewPlaceAdd.layoutManager = LinearLayoutManager(itemView.context)

            // add_place_btn 클릭 시 새로운 장소 추가
            binding.addPlaceBtn.setOnClickListener {
                val newItem = ""
                placeAddAdapter.addItem(newItem)
            }

            // 날짜 입력 DatePickerDialog 사용
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1 // 현재 월 가져오기 (월은 0부터 시작하므로 +1)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            binding.travelDateTv.text = "$year.$month.$day" // 텍스트뷰에 날짜 표시

            binding.dateSettingBtn.setOnClickListener {
                val cal = Calendar.getInstance()
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)

                val data = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val formattedMonth = month + 1 // 선택된 월 값 보정 (월은 0부터 시작하므로 +1)
                    binding.travelDateTv.text =
                        "${year}. ${formattedMonth}. ${day}"  // 선택된 날짜 텍스트뷰에 표시
                }

                DatePickerDialog(
                    itemView.context,
                    data,
                    year,
                    month,
                    day
                ).show()  // DatePickerDialog를 열어 날짜 선택 가능하도록 함
            }

        }
    }

    // CheckList 추가
    fun addItem(item: String) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }
}

