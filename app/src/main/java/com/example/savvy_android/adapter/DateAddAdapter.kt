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
    private var firstItemDate: Calendar? = null // 첫 번째 아이템의 날짜 저장

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

            // 아이템 삭제 버튼 클릭 시 해당 위치의 아이템을 제거하고 새로고침
            binding.icX.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    data.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            //첫번째 아이템에서만 dialog 띄울 수 있음
            if (position == 0) {
                // 첫 번째 아이템인 경우
                val currentDate = Calendar.getInstance()
                if (firstItemDate == null) {
                    // 첫 번째 아이템의 날짜가 설정되지 않은 경우 - 오늘 날짜
                    firstItemDate = currentDate
                }
                binding.travelDateTv.text = formatDate(firstItemDate!!)

                // 날짜 설정 버튼 클릭 시 DateDialogFragment 띄우기
                binding.travelDateTv.setOnClickListener {
                    val dateDialogFragment = DateDialogFragment()
                    dateDialogFragment.setCurrentDate(firstItemDate)
                    dateDialogFragment.setOnDateSelectedListener { year, month, day ->
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(year, month, day)
                        firstItemDate = selectedDate
                        notifyDataSetChanged()
                        binding.travelDateTv.text = formatDate(selectedDate)
                    }
                    dateDialogFragment.show(fragmentManager, "DateDialog")
                }
            } else {
                // 첫 번째 아이템이 아닌 경우
                val newDate = firstItemDate!!.clone() as Calendar //firstItemDate를 기준으로
                newDate.add(Calendar.DAY_OF_MONTH, position)  // position일 수 만큼 더한 날짜를 계산
                binding.travelDateTv.text = formatDate(newDate)
            }
        }

        // 첫 번째 아이템이 아닌 경우, 새로운 날짜를 계산하여 travelDateTv에 표시
        private fun formatDate(date: Calendar): String {
            val year = date.get(Calendar.YEAR)
            val month = date.get(Calendar.MONTH) + 1
            val day = date.get(Calendar.DAY_OF_MONTH)
            return "$year.$month.$day"
        }
    }
    // DateAdd 추가
    fun addItem(item: String) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }
}


