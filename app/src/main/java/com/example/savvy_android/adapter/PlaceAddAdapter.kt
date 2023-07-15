package com.example.savvy_android.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.activity.PlaceAddActivity
import com.example.savvy_android.databinding.ItemPlaceAddBinding
import com.example.savvy_android.dialog.TimeDialogFragment

class PlaceAddAdapter(private val data: MutableList<String>, private val fragmentManager: FragmentManager) :
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


            binding.travelPlanTime.setOnClickListener {
                val timeDialogFragment = TimeDialogFragment()

                timeDialogFragment.setOnTimeSelectedListener { hour1, minute1, hour2, minute2 ->
                    val formattedTime1 = String.format("%02d:%02d", hour1, minute1)
                    val formattedTime2 = String.format("%02d:%02d", hour2, minute2)

                    binding.travelPlanTimeTv.text = formattedTime1
                    binding.travelPlanTimeTv3.text = formattedTime2
                }


                timeDialogFragment.show(fragmentManager, "TimeDialog")
            }

        }
    }

    // PlaceAdd 추가
    fun addItem(item: String) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }
}