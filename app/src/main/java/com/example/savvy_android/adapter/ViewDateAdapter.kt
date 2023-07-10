package com.example.savvy_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.activity.TravelPlanViewActivity
import com.example.savvy_android.databinding.ItemViewDateBinding

class ViewDateAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<ViewDateAdapter.ViewHolder>() {
    private val placeMap: MutableMap<Int, ViewPlaceAdapter> = mutableMapOf()

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private val binding: ItemViewDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var viewplaceAdapter: ViewPlaceAdapter

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: String, position: Int) {

            // placeAdd RecyclerView 설정
            viewplaceAdapter = ViewPlaceAdapter(mutableListOf("", ""))
            placeMap[position] = viewplaceAdapter
            binding.recyclerviewViewPlace.adapter = viewplaceAdapter
            binding.recyclerviewViewPlace.layoutManager = LinearLayoutManager(itemView.context)

// arrowDownBtn 클릭 이벤트 처리
            binding.arrowUpBtn.setOnClickListener {
                val newView = LayoutInflater.from(binding.root.context).inflate(
                    R.layout.item_view_date_only,
                    binding.root,
                    false
                )
                binding.root.removeAllViews()
                binding.root.addView(newView)
            }
        }

    }
}

