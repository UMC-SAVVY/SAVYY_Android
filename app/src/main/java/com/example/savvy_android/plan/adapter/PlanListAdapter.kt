package com.example.savvy_android.plan.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.databinding.ItemPlanBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.activity.PlanDetailVisitActivity
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import com.example.savvy_android.plan.data.list.PlanListResult
import com.example.savvy_android.plan.dialog.PlanDeleteDialogFragment
import com.example.savvy_android.plan.service.PlanListService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlanListAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val myName: String,
    private val fragmentManager: FragmentManager,
    private val isPlan: Boolean,
    private val optionVisible: Boolean
) :
    ListAdapter<PlanListResult, PlanListAdapter.PlanViewHolder>(diffUtil) {

    // 각 뷰들을 binding 사용하여 View 연결
    inner class PlanViewHolder(var binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var item = binding.itemPlanIn
        var title = binding.itemPlanTitle
        var date = binding.itemPlanDate
        var user = binding.itemPlanUser
        var hideX = binding.itemPlanHideX
        var hideO = binding.itemPlanHideO
        var scrollHolder = binding.itemPlanArrow

        fun bind(data: PlanListResult, position: Int) {
            binding.apply {
                binding.itemPlanTitle.text = data.title
                binding.itemPlanDate.text = data.updated_at
                binding.itemPlanUser.text = if (data.nickname == myName) "" else data.nickname

                // 숨겨진 삭제 버튼 클릭 이벤트
                binding.itemPlanHideO.setOnClickListener {
                    val dialog = PlanDeleteDialogFragment()

                    // 다이얼로그 버튼 클릭 이벤트 설정
                    dialog.setButtonClickListener(object :
                        PlanDeleteDialogFragment.OnButtonClickListener {
                        override fun onDialogPlanBtnOClicked() {
                            planRemoveAPI(
                                plannerId = data.id.toString(),
                                plannerType = if (binding.itemPlanUser.text == "") "0" else "1",
                                position = position
                            )
                        }

                        override fun onDialogPlanBtnXClicked() {
                            resetHideX(position, recyclerView)
                        }
                    })
                    dialog.show(fragmentManager, "PlanDeleteDialog")
                }

                // 아이템 클릭 이벤트 (여행 계획서 보기)
                itemView.setOnClickListener {
                    if (hasSwipe) {
                        resetHideX(hasSwipePosition, recyclerView)
                    } else {
                        // 자신의 여행 계획서 경우
                        if (data.nickname == myName || data.nickname == null) {
                            // 여행계획서 리스트에서 option visible
                            if(optionVisible == true){
                                val mIntent =
                                    Intent(itemView.context, PlanDetailActivity::class.java)
                                mIntent.putExtra("planID", data.id)
                                mIntent.putExtra("visibleOption", true)
                                itemView.context.startActivity(mIntent)
                            }
                            else{
                                // 마이페이지 계획서 리스트에서 option visible
                                val mIntent =
                                    Intent(itemView.context, PlanDetailActivity::class.java)
                                mIntent.putExtra("planID", data.id)
                                mIntent.putExtra("visibleOption", false)
                                itemView.context.startActivity(mIntent)

                            }
                        }
                        // 타인의 여행 계획서 경우
                        else {
                            val mIntent =
                                Intent(itemView.context, PlanDetailVisitActivity::class.java)
                            mIntent.putExtra("planID", data.id)
                            itemView.context.startActivity(mIntent)
                        }
                    }
                }

                binding.itemPlanHideO.isClickable = false // 초기 클릭 가능 상태 설정

                // 계획서 title scroll
                binding.itemPlanTitle.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        binding.itemPlanTitle.viewTreeObserver.removeOnPreDrawListener(this)

                        binding.itemPlanTitle.apply {
                            setSingleLine()
                            marqueeRepeatLimit = -1
                            ellipsize = TextUtils.TruncateAt.MARQUEE
                            isSelected = true
                        }
                        return true
                    }
                })
            }
        }
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlanViewHolder {
        val binding =
            ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        // 계획서 페이지 or 마이페이지 구분에 따라 스크롤 레이아웃 시각화 설정
        if (isPlan)
            holder.scrollHolder.visibility = View.VISIBLE
        else
            holder.scrollHolder.visibility = View.GONE

        holder.bind(currentList[position], position)
    }

    // 데이터 삭제
    private fun removePlan(position: Int) {
        if (position in 0 until currentList.size) {
            val updatedList = currentList.toMutableList()
            updatedList.removeAt(position)
            submitList(updatedList)
        }
    }

    // 스와이프로 고정된 상태 해제
    fun clearSwipe() {
        if (hasSwipe) {
            val changeHolder =
                recyclerView.findViewHolderForAdapterPosition(hasSwipePosition) as? PlanViewHolder
            if (changeHolder != null) {
                changeHolder.itemView.tag = false // 스와이프로 고정된 상태 해제
                changeHolder.hideO.isClickable = false // 클릭 불가능
                changeHolder.hideX.translationX = 0f // 위치 값 0
                hasSwipe = false
            }
        }
    }


    // swipe 상태인 아이템 존재 유무
    companion object {
        var hasSwipe: Boolean = false
        var hasSwipePosition: Int = 0

        // 삭제 버튼 안 보이고, 작동 안 하도록
        fun resetHideX(position: Int, recyclerView: RecyclerView) {
            // 움직일 아이템의 holder
            val changeHolder =
                recyclerView.findViewHolderForAdapterPosition(position) as? PlanViewHolder
            if (changeHolder != null) {
                changeHolder.itemView.tag = false // 스와이프로 고정된 상태 해제
                changeHolder.hideO.isClickable = false // 클릭 불가능
                // 애니메이션 추가
                val animator = ObjectAnimator.ofFloat(
                    changeHolder.hideX,
                    "translationX",
                    changeHolder.hideX.translationX,    // changeHolder.hideX의 현재 translationX 값
                    0f  // 애니메이션의 종료 값
                )
                animator.duration = 200 // 애니메이션 지속 시간 (밀리초)
                animator.start()
                hasSwipe = false    // swipe된 아이템 없음
            }
        }

        val diffUtil = object : DiffUtil.ItemCallback<PlanListResult>() {
            override fun areItemsTheSame(
                oldItem: PlanListResult,
                newItem: PlanListResult,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PlanListResult,
                newItem: PlanListResult,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    // 여행계획서 삭제 API
    private fun planRemoveAPI(plannerId: String, plannerType: String, position: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val planListService = retrofit.create(PlanListService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // Delete 요청
        planListService.planDelete(
            token = accessToken,
            plannerId = plannerId,
            plannerType = plannerType,
        )
            .enqueue(object : Callback<ServerDefaultResponse> {
                override fun onResponse(
                    call: Call<ServerDefaultResponse>,
                    response: Response<ServerDefaultResponse>,
                ) {
                    if (response.isSuccessful) {
                        val deleteResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (deleteResponse?.isSuccess == true) {
                            removePlan(position)

                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("성공적으로 계획서가 삭제되었습니다.")
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                context.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "PLAN",
                                    detailType = "DELETE",
                                    intentData = null
                                )
                            }

                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("계획서 삭제를 실패하였습니다.")
                        }
                    } else {
                        Log.e(
                            "PLAN",
                            "[PLAN DELETE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )

                        // 삭제 성공 시 토스트 메시지 표시
                        showToast("계획서 삭제를 실패하였습니다.")
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("PLAN", "[PLAN DELETE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                    // 삭제 성공 시 토스트 메시지 표시
                    showToast("계획서 삭제를 실패하였습니다.")
                }
            })
    }

    // 토스트 메시지 표시 함수 추가
    private fun showToast(message: String) {
        val toastBinding = LayoutToastBinding.inflate(LayoutInflater.from(context))
        toastBinding.toastMessage.text = message
        val toast = Toast(context)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

}
