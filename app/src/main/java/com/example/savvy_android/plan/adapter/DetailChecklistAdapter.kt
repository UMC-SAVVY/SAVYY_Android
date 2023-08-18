package com.example.savvy_android.plan.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemPlanDetailChecklistBinding
import com.example.savvy_android.plan.data.Checklist
import com.example.savvy_android.plan.data.DetailChecklist
import com.example.savvy_android.plan.data.PlanCheckRequest
import com.example.savvy_android.plan.data.PlanCheckResponse
import com.example.savvy_android.plan.service.DetailCheckService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailChecklistAdapter(private val items: MutableList<Checklist>,
                             private val context: Context,
                             private val isMine: Boolean) :
    RecyclerView.Adapter<DetailChecklistAdapter.ViewCheckListViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의

    inner class ViewCheckListViewHolder(private val binding: ItemPlanDetailChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(item: Checklist) {

            binding.travelPlanViewChecklistTv.text = item.contents

            // 여행계획서에 들어갔을 때 is_checked의 0 or 1에 따른 색 변화
            if (item.is_checked == 1) {
                // is_checked가 1이면 color/main
                binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.border_circle_main)
                binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_main))
            } else {
                // is_checked가 0이면 원래 color
                binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.border_circle_gray)
                binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_gray))
            }

            if(isMine){
                binding.check.setOnClickListener {
                    // 클릭 시 Drawable 변경
                    item.is_checked = if (item.is_checked == 1) 0 else 1
                    if (item.is_checked == 1) {
                        // 클릭 시 Drawable 변경
                        binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.border_circle_main)
                        binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_main))
                    } else {
                        // 다시 클릭 시 원래의 Drawable로 복원
                        binding.borderCircleGray.background = itemView.context.getDrawable(R.drawable.border_circle_gray)
                        binding.checkmarkGray.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_checkmark_gray))
                    }

                    updateChecklistOnServer(item)
                }
            }else{

            }
        }
    }

    // 새로운 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCheckListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlanDetailChecklistBinding.inflate(inflater, parent, false)
        return ViewCheckListViewHolder(binding)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewCheckListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // 데이터 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    private fun updateChecklistOnServer(item: Checklist) {

        sharedPreferences = context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val detailCheckService = retrofit.create(DetailCheckService::class.java)

        val checklistToUpdate = mutableListOf<DetailChecklist>()
        checklistToUpdate.add(DetailChecklist(item.id, item.contents, item.is_checked))

        val request = PlanCheckRequest(checklistToUpdate)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        detailCheckService.detailCheck(serverToken, request)
            .enqueue(object : Callback<PlanCheckResponse> {
                override fun onResponse(
                    call: Call<PlanCheckResponse>,
                    response: Response<PlanCheckResponse>
                ) {
                    if (response.isSuccessful) {
                        val planChecklistResponse = response.body()
                        val isSuccess = planChecklistResponse?.isSuccess
                        val code = planChecklistResponse?.code
                        val message = planChecklistResponse?.message

                        // 서버 응답 처리 로직 작성 (업데이트 성공 여부에 따른 처리)
                        Log.d("ChecklistUpdate", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                    } else {
                        // 응답 오류 처리
                        Log.e("ChecklistUpdate", "API 호출 실패 - 응답 코드: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PlanCheckResponse>, t: Throwable) {
                    // 통신 실패 처리
                    Log.e("ChecklistUpdate", "통신 실패 - ${t.message}")
                }
            })
    }

}
