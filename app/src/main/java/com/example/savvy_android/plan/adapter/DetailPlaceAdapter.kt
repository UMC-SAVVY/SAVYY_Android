package com.example.savvy_android.plan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.databinding.ItemPlanPlaceBinding
import com.example.savvy_android.plan.data.Schedule

class DetailPlaceAdapter(private val data: MutableList<Schedule>,
                         private val isMine: Boolean) :
    RecyclerView.Adapter<DetailPlaceAdapter.ViewHolder>() {
//    private val viewCheckListMap: MutableMap<Int, DetailChecklistAdapter> = mutableMapOf()
    private var initialCircleNum = 1


    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlanPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private val binding: ItemPlanPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var viewChecklistAdapter: DetailChecklistAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Schedule, position: Int) {

            binding.planViewPlaceNameTv.text = item.place_name
            binding.planViewTimeTv1.text = item.started_at
            binding.planViewTimeTv3.text = item.finished_at

            val circleNum = initialCircleNum + position
            binding.step.text = circleNum.toString()

//            // checkList RecyclerView 설정
//            viewChecklistAdapter = DetailChecklistAdapter(item.checklist)
//            viewCheckListMap[position] = viewChecklistAdapter
//            binding.recyclerviewViewChecklist.adapter = viewChecklistAdapter
//            binding.recyclerviewViewChecklist.layoutManager = LinearLayoutManager(itemView.context)

            if(isMine){
                // DetailActivity에 접근하면 에러 발생
                // checklist가 null인지 확인한 후 어댑터를 생성
                if (item.checklist != null) {
                    viewChecklistAdapter = DetailChecklistAdapter(item.checklist, itemView.context, true)
                    binding.recyclerviewViewChecklist.adapter = viewChecklistAdapter
                    binding.recyclerviewViewChecklist.layoutManager = LinearLayoutManager(itemView.context)
                } else {
                    // checklist가 null인 경우 처리 방법
                    // 예를 들어, 빈 checklist를 생성하여 NullPointerException을 피할 수 있음
                    viewChecklistAdapter = DetailChecklistAdapter(item.checklist, itemView.context, true)
                    binding.recyclerviewViewChecklist.adapter = viewChecklistAdapter
                    binding.recyclerviewViewChecklist.layoutManager = LinearLayoutManager(itemView.context)
                }
            }else{
                // DetailActivity에 접근하면 에러 발생
                // checklist가 null인지 확인한 후 어댑터를 생성
                if (item.checklist != null) {
                    viewChecklistAdapter = DetailChecklistAdapter(item.checklist, itemView.context, false)
                    binding.recyclerviewViewChecklist.adapter = viewChecklistAdapter
                    binding.recyclerviewViewChecklist.layoutManager = LinearLayoutManager(itemView.context)
                } else {
                    // checklist가 null인 경우 처리 방법
                    // 예를 들어, 빈 checklist를 생성하여 NullPointerException을 피할 수 있음
                    viewChecklistAdapter = DetailChecklistAdapter(item.checklist, itemView.context, false)
                    binding.recyclerviewViewChecklist.adapter = viewChecklistAdapter
                    binding.recyclerviewViewChecklist.layoutManager = LinearLayoutManager(itemView.context)
                }
            }
        }
    }

}
