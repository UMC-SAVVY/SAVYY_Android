package com.example.savvy_android.plan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemPlanMakeDateAddBinding
import com.example.savvy_android.plan.data.Checklist
import com.example.savvy_android.plan.data.Schedule
import com.example.savvy_android.plan.data.Timetable
import com.example.savvy_android.plan.dialog.DateDialogFragment
import java.util.Calendar

class MakeDateAddAdapter(private val data: MutableList<Timetable>,
                         private val fragmentManager: FragmentManager,
                         var isMake: Boolean
) :
    RecyclerView.Adapter<MakeDateAddAdapter.ViewHolder>() {
    private val placeAddMap: MutableMap<Int, MakePlaceAddAdapter> = mutableMapOf()
    private var firstItemDate: Calendar? = null // 첫 번째 아이템의 날짜 저장

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlanMakeDateAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private val binding: ItemPlanMakeDateAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var placeAddAdapter: MakePlaceAddAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Timetable, position: Int) {


            if(isMake){
                // placeAdd RecyclerView 설정
                placeAddAdapter = MakePlaceAddAdapter(item.schedule, fragmentManager, true)
                placeAddMap[position] = placeAddAdapter
                binding.recyclerviewPlaceAdd.adapter = placeAddAdapter
                binding.recyclerviewPlaceAdd.layoutManager = LinearLayoutManager(itemView.context)
            }else{
                // placeAdd RecyclerView 설정
                placeAddAdapter = MakePlaceAddAdapter(item.schedule, fragmentManager, false)
                placeAddMap[position] = placeAddAdapter
                binding.recyclerviewPlaceAdd.adapter = placeAddAdapter
                binding.recyclerviewPlaceAdd.layoutManager = LinearLayoutManager(itemView.context)

                binding.travelDateTv.text = item.date
            }

            // add_place_btn 클릭 시 새로운 장소 추가
            binding.addPlaceBtn.setOnClickListener {
                val newSchedule = Schedule(null, mutableListOf(Checklist(null, "", 0)), "", "", "")
                placeAddAdapter.addItem(newSchedule)
            }

            if (position == 0) {
                // 첫 번째 아이템인 경우에는 icX를 GONE으로 설정
                binding.icX.visibility = View.GONE
            } else {
                // 첫 번째 아이템이 아닌 경우에는 icX를 보이도록 설정
                binding.icX.visibility = View.VISIBLE

                // 아이템 삭제 버튼 클릭 시 해당 위치의 아이템을 제거하고 새로고침
                binding.icX.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        data.removeAt(position)
                        notifyItemRemoved(position)
                    }
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

            val updatedTimetable = Timetable(
                date = binding.travelDateTv.text.toString(),
                schedule = placeAddAdapter.getDataList() // MakePlaceAddAdapter의 데이터를 가져옴
            )
            data[position] = updatedTimetable
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
    fun addItem(item: Timetable) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }

    fun getDataList(): MutableList<Timetable> {
        return data
    }

    fun addAllItems(item: MutableList<Timetable>) {
        data.addAll(item)
        this.notifyDataSetChanged()
    }

}


// 지금 문제는 isMake true는 작성, false는 수정으로 생각
// 수정 액티비티 에서는 현재 false를 사용하고 있고 아이템을 추가할 때만 true를 사용함
// 현재 dateAdapter에서 날짜를 수정할 때 작성(true)에서는 첫번째 아이템이 오늘 날짜, 그 뒤에 오늘 날짜의 +1로 추가 (작성)
// 수정에서는 item.date에서 날짜를 받아서 띄운 뒤 새로운 날짜가 마지막 날짜의 +1이 되도록 해야 함
// 하지만 수정 액티비티에서 아이템을 사용할 때 true를 사용하고 있어서 발생하는 에러인듯
// true에서는 무조건 첫번째 아이템이 오늘 날짜가 되어야 하고...?
// 어쩌지