package com.example.savvy_android.plan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemPlanDateBinding
import com.example.savvy_android.plan.data.Timetable

class DetailDateAdapter(private val items: MutableList<Timetable>,
                        private val isMine: Boolean) :
    RecyclerView.Adapter<DetailDateAdapter.ViewDateViewHolder>() {
    private val placeMap: MutableMap<Int, DetailPlaceAdapter> = mutableMapOf()
    private var expandedPosition = 0 // 첫 번째 아이템의 인덱스


    inner class ViewDateViewHolder(private val binding: ItemPlanDateBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var isExpanded = false
        private lateinit var viewplaceAdapter: DetailPlaceAdapter

        init {
            binding.arrowDownBtn.setOnClickListener(this)
        }

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Timetable) {
            // 아이템 데이터 바인딩 처리

            binding.travelPlanViewDateTv.text = item.date

            if(isMine){
                // placeAdd RecyclerView 설정
                viewplaceAdapter = DetailPlaceAdapter(item.schedule, true)
                placeMap[adapterPosition] = viewplaceAdapter // adapterPosition 사용
                binding.recyclerviewViewPlace.adapter = viewplaceAdapter
                binding.recyclerviewViewPlace.layoutManager = LinearLayoutManager(itemView.context)

            }else{
                // placeAdd RecyclerView 설정
                viewplaceAdapter = DetailPlaceAdapter(item.schedule, false)
                placeMap[adapterPosition] = viewplaceAdapter // adapterPosition 사용
                binding.recyclerviewViewPlace.adapter = viewplaceAdapter
                binding.recyclerviewViewPlace.layoutManager = LinearLayoutManager(itemView.context)

            }

            // 초기 상태 설정
            if (adapterPosition == expandedPosition) {
                binding.line.visibility = View.VISIBLE
                binding.recyclerviewViewPlace.visibility = View.VISIBLE
                binding.arrowDownBtn.setImageResource(R.drawable.ic_arrow_up)
                isExpanded = true
            } else {
                binding.line.visibility = View.GONE
                binding.recyclerviewViewPlace.visibility = View.GONE
                binding.arrowDownBtn.setImageResource(R.drawable.ic_arrow_down)
                isExpanded = false
            }


        }

        // 클릭 이벤트 처리
        override fun onClick(view: View) {
            if (view.id == R.id.arrow_down_btn) {
                isExpanded = !isExpanded // 상태를 토글로 변경

                if (isExpanded) {
                    // 숨겨진 부분을 펼치는 로직 구현
                    binding.line.visibility = View.VISIBLE
                    binding.recyclerviewViewPlace.visibility = View.VISIBLE
                    binding.arrowDownBtn.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    // 숨겨진 부분을 접는 로직 구현
                    binding.line.visibility = View.GONE
                    binding.recyclerviewViewPlace.visibility = View.GONE
                    binding.arrowDownBtn.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewDateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlanDateBinding.inflate(inflater, parent, false)
        return ViewDateViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewDateViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    fun getDataList(): MutableList<Timetable> {
        return items
    }

    fun addAllItems(item: MutableList<Timetable>) {
        items.addAll(item)
        this.notifyDataSetChanged()
    }
}
