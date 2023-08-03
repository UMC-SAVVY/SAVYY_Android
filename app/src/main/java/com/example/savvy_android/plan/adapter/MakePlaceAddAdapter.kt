package com.example.savvy_android.plan.adapter

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.utils.place.PlaceAddActivity
import com.example.savvy_android.databinding.ItemPlaceAddBinding
import com.example.savvy_android.plan.data.Checklist
import com.example.savvy_android.plan.data.Schedule
import com.example.savvy_android.plan.dialog.TimeDialogFragment

class MakePlaceAddAdapter(private val data: MutableList<Schedule>,
                          private val fragmentManager: FragmentManager,
                          private var isMake: Boolean
) :
    RecyclerView.Adapter<MakePlaceAddAdapter.ViewHolder>() {
//    private val checkListMap: MutableMap<Int, MakeCheckListAdapter> = mutableMapOf()


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

        private lateinit var checkListAdapter: MakeCheckListAdapter


        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Schedule, position: Int) {

            if(isMake){
                // 이 코드 때문에 날짜 아이템 추가하고 날짜 바꾸면 에러남
                // 기본으로 두 개의 빈 checklist 아이템 추가
                if (item.checklist.size < 2) {
                    item.checklist.add(Checklist(null, "", 0))
                }
            }else{
                binding.placeNameEdit.setText(item.place_name)
                binding.travelPlanTimeTv.text = item.started_at
                binding.travelPlanTimeTv3.text = item.finished_at
            }



//            // checkList RecyclerView 설정
//            checkListAdapter = MakeCheckListAdapter(item.checklist)
//            checkListMap[position] = checkListAdapter
//            binding.recyclerviewChecklist.adapter = checkListAdapter
//            binding.recyclerviewChecklist.layoutManager = LinearLayoutManager(itemView.context)

            if (item.checklist != null) {
                checkListAdapter = MakeCheckListAdapter(item.checklist)
                binding.recyclerviewChecklist.adapter = checkListAdapter
                binding.recyclerviewChecklist.layoutManager = LinearLayoutManager(itemView.context)
            } else {
                // checklist가 null인 경우 처리 방법
                // 예를 들어, 빈 checklist를 생성하여 NullPointerException을 피할 수 있음
                checkListAdapter = MakeCheckListAdapter(mutableListOf())
                binding.recyclerviewChecklist.adapter = checkListAdapter
                binding.recyclerviewChecklist.layoutManager = LinearLayoutManager(itemView.context)
            }



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
                val newItem = Checklist(null, "", 0)
                checkListAdapter.addItem(newItem)
            }

            // ic_add_place 클릭 시 PlaceAddActivity로 넘어가기
            binding.icAddPlace.setOnClickListener {
                val intent = Intent(binding.root.context, PlaceAddActivity::class.java)
                binding.root.context.startActivity(intent)
            }

            binding.placeNameEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    data[position].place_name = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })


            binding.travelPlanTime.setOnClickListener {
                val timeDialogFragment = TimeDialogFragment()

                timeDialogFragment.setOnTimeSelectedListener { hour1, minute1, hour2, minute2 ->
                    val formattedTime1 = String.format("%02d:%02d", hour1, minute1)
                    val formattedTime2 = String.format("%02d:%02d", hour2, minute2)

                    binding.travelPlanTimeTv.text = formattedTime1
                    binding.travelPlanTimeTv3.text = formattedTime2

                    val placeName = binding.placeNameEdit.text.toString()

                    val updatedSchedule = Schedule(
                        id = null,
                        place_name = placeName,
                        started_at = formattedTime1,
                        finished_at = formattedTime2,
                        checklist = checkListAdapter.getDataList()
                    )
                    data[position] = updatedSchedule
                }
                timeDialogFragment.show(fragmentManager, "TimeDialog")
            }
        }
    }

    // PlaceAdd 추가
    fun addItem(item: Schedule) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }

    fun getDataList(): MutableList<Schedule> {
        return data
    }


}