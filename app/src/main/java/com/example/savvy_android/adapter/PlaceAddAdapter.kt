package com.example.savvy_android.adapter

import android.app.TimePickerDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.activity.PlaceAddActivity
import com.example.savvy_android.databinding.ItemPlaceAddBinding
import java.util.Calendar

class PlaceAddAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<PlaceAddAdapter.ViewHolder>() {
    private val checkListMap: MutableMap<Int, CheckListAdapter> = mutableMapOf()


    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private val binding: ItemPlaceAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var checkListAdapter: CheckListAdapter


        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String, position: Int) {
            // checkList RecyclerView 설정
            checkListAdapter = CheckListAdapter(mutableListOf("", ""))
            checkListMap[position] = checkListAdapter
            binding.recyclerviewChecklist.adapter = checkListAdapter
            binding.recyclerviewChecklist.layoutManager = LinearLayoutManager(itemView.context)


            // 아이템 삭제 버튼 클릭 시 해당 위치의 아이템을 제거하고 새로고침
            binding.icX.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    data.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            // addChecklistBtn 클릭 시 새로운 체크 리스트 추가
            binding.addChecklistBtn.setOnClickListener {
                val newItem = ""
                checkListAdapter.addItem(newItem)
            }

            // ic_add_place 클릭 시 PlaceAddActivity로 넘어가기
            binding.icAddPlace.setOnClickListener {
                val intent = Intent(binding.root.context, PlaceAddActivity::class.java)
                binding.root.context.startActivity(intent)
            }


            // 시간 입력 TimePickerDialog 사용
            binding.travelPlanTimeTv.setOnClickListener {
                val cal = Calendar.getInstance()
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                val minute = cal.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(
                    itemView.context,
                    { _, hourOfDay, minute ->
                        val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                        binding.travelPlanTimeTv.text = selectedTime
                    },
                    hour,
                    minute,
                    true
                )
                timePickerDialog.show()
            }

            binding.travelPlanTimeTv3.setOnClickListener {
                val cal = Calendar.getInstance()
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                val minute = cal.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(
                    itemView.context,
                    { _, hourOfDay, minute ->
                        val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                        binding.travelPlanTimeTv3.text = selectedTime
                    },
                    hour,
                    minute,
                    true
                )
                timePickerDialog.show()
            }

        }
    }

    // PlaceAdd 추가
    fun addItem(item: String) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }
}