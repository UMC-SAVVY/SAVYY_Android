package com.example.savvy_android

import android.app.TimePickerDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.PlaceAddItemBinding
import java.util.Calendar

class PlaceAddAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<PlaceAddAdapter.ViewHolder>() {
    private lateinit var checkListAdapter: CheckListAdapter


    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceAddAdapter.ViewHolder {
        val binding =
            PlaceAddItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: PlaceAddAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: PlaceAddItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String) {
            // checkList RecyclerView 설정
            checkListAdapter = CheckListAdapter(mutableListOf("", ""))
            binding.recyclerviewChecklist.adapter = checkListAdapter
            binding.recyclerviewChecklist.layoutManager = LinearLayoutManager(itemView.context)

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
