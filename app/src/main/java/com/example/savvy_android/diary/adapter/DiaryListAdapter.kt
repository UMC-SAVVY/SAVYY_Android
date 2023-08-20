package com.example.savvy_android.diary.adapter

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
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.diary.activity.DiaryDetailActivity
import com.example.savvy_android.databinding.ItemDiaryBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.data.list.DiaryListResult
import com.example.savvy_android.diary.dialog.DiaryDeleteDialogFragment
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DiaryListAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val fragmentManager: FragmentManager,
    private val isDiary: Boolean,
) :
    ListAdapter<DiaryListResult, DiaryListAdapter.DiaryViewHolder>(diffUtil) {

    // 각 뷰들을 binding 사용하여 View 연결
    inner class DiaryViewHolder(var binding: ItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var item = binding.itemDiaryIn
        var title = binding.itemDiaryTitle
        var date = binding.itemDiaryDate
        private var like = binding.itemDiaryLikeTv
        var comment = binding.itemDiaryCommentTv
        var hideX = binding.itemDiaryHideX
        private var photoCard = binding.itemDiaryPhotoCv
        private var photoImg = binding.itemDiaryPhotoIv
        private var photoCount = binding.itemDiaryPhotoTv
        var hideODelete = binding.itemDiaryHideODelete
        var hideOShow = binding.itemDiaryHideOShow
        var showImg = binding.itemDiaryShowIv
        var showText = binding.itemDiaryShowTv
        var scrollHolder = binding.itemDiaryArrow

        fun bind(data: DiaryListResult, position: Int) {
            val holder = this
            binding.apply {
                binding.itemDiaryTitle.text = data.title
                binding.itemDiaryDate.text = data.updated_at
                like.text = data.likes_count.toString()
                comment.text = data.comments_count.toString()
                if (data.img_count > 0) {
                    photoCard.isVisible = true
                    photoCount.setBackgroundResource(if (data.img_count == 1) 0 else R.color.gray50)
                    photoCount.text = if (data.img_count == 1) "" else "+${data.img_count}"
                    Glide.with(itemView)
                        .load(data.thumbnail)
                        .into(photoImg)
                } else {
                    photoCard.isVisible = false
                }
                showResource(data.is_public, holder)

                // 숨겨진 공개 여부 버튼 클릭 이벤트
                hideOShow.setOnClickListener {
                    publicAPI(diaryId = data.id, position = position, holder = holder)
                }

                // 숨겨진 삭제 버튼 클릭 이벤트
                hideODelete.setOnClickListener {
                    val dialog = DiaryDeleteDialogFragment()

                    // 다이얼로그 버튼 클릭 이벤트 설정
                    dialog.setButtonClickListener(object :
                        DiaryDeleteDialogFragment.OnButtonClickListener {
                        override fun onDialogBtnOClicked() {
                            diaryRemoveAPI(
                                diaryId = data.id.toString(),
                                position = adapterPosition
                            )
                        }

                        override fun onDialogBtnXClicked() {
                            resetHideX(adapterPosition, recyclerView)
                        }
                    })
                    dialog.show(fragmentManager, "DiaryDeleteDialog")
                }

                // 아이템 클릭 이벤트 (다이어리 상세보기)
                itemView.setOnClickListener {
                    if (hasSwipe) {
                        resetHideX(hasSwipePosition, recyclerView)
                    } else {
                        val mIntent =
                            Intent(itemView.context, DiaryDetailActivity::class.java)
                        mIntent.putExtra("diaryID", data.id)
                        itemView.context.startActivity(mIntent)
                    }
                }

                hideODelete.isClickable = false // 초기 클릭 가능 상태 설정
                hideOShow.isClickable = false // 초기 클릭 가능 상태 설정


                // 계획서 title scroll
                title.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        title.viewTreeObserver.removeOnPreDrawListener(this)

                        title.apply {
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
    ): DiaryViewHolder {
        val binding =
            ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        // 다이어리 페이지 or 마이페이지 구분에 따라 스크롤 레이아웃 시각화 설정
        if (isDiary)
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
    fun clearList() {
        if (hasSwipe) {
            val changeHolder =
                recyclerView.findViewHolderForAdapterPosition(hasSwipePosition) as? DiaryViewHolder
            if (changeHolder != null) {
                changeHolder.itemView.tag = false // 스와이프로 고정된 상태 해제
                changeHolder.hideODelete.isClickable = false // 클릭 불가능
                changeHolder.hideODelete.isClickable = false // 클릭 불가능
                changeHolder.hideX.translationX = 0f // 위치 값 0
                hasSwipe = false
            }
        }
    }

    // 공개 비공개 여부에 따른 리소스
    private fun showResource(isShow: Boolean, holder: DiaryViewHolder) {
        if (isShow) {
            holder.hideOShow.setBackgroundResource(R.color.green)
            holder.showImg.setImageResource(R.drawable.ic_eye_on)
            holder.showText.text = "공개"
        } else {
            holder.hideOShow.setBackgroundResource(R.color.button_line)
            holder.showImg.setImageResource(R.drawable.ic_eye_off)
            holder.showText.text = "비공개"
        }
    }


    // swipe 상태인 아이템 존재 유무
    companion object {
        var hasSwipe: Boolean = false
        var hasSwipePosition: Int = 0

        // 숨겨진 레이어 안 보이고, 작동 안 하도록
        fun resetHideX(position: Int, recyclerView: RecyclerView) {
            // 움직일 아이템의 holder
            val changeHolder =
                recyclerView.findViewHolderForAdapterPosition(position) as? DiaryViewHolder
            if (changeHolder != null) {
                changeHolder.itemView.tag = false // 스와이프로 고정된 상태 해제
                changeHolder.hideODelete.isClickable = false // 클릭 불가능
                changeHolder.hideODelete.isClickable = false // 클릭 불가능
                // 애니메이션 추가
                val animator = ObjectAnimator.ofFloat(
                    changeHolder.hideX,
                    "translationX",
                    changeHolder.hideX.translationX,    // changeHolder.hideX의 현재 translationX 값
                    0f  // 애니메이션의 종료 값
                )
                animator.duration = 400 // 애니메이션 지속 시간 (밀리초)
                animator.start()
                hasSwipe = false    // swipe된 아이템 없음
            }
        }

        val diffUtil = object : DiffUtil.ItemCallback<DiaryListResult>() {
            override fun areItemsTheSame(
                oldItem: DiaryListResult,
                newItem: DiaryListResult,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DiaryListResult,
                newItem: DiaryListResult,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    // 다이어리 삭제 API
    private fun diaryRemoveAPI(diaryId: String, position: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val planListService = retrofit.create(DiaryService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // Delete 요청
        planListService.diaryDelete(
            token = accessToken,
            diaryID = diaryId
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
                            showToast("성공적으로 다이어리가 삭제되었습니다.")
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                context.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "DELETE",
                                    intentData = null
                                )
                            }
                            // 삭제 실패 시 토스트 메시지 표시
                            showToast("계획서 삭제를 실패하였습니다.")
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY DELETE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                        // 삭제 실패 시 토스트 메시지 표시
                        showToast("계획서 삭제를 실패하였습니다.")
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("DIARY", "[DIARY DELETE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                    // 삭제 실패 시 토스트 메시지 표시
                    showToast("계획서 삭제를 실패하였습니다.")
                }
            })
    }

    private fun publicAPI(diaryId: Int, position: Int, holder: DiaryViewHolder) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val diaryStatusService = retrofit.create(DiaryService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // 공개 변경 요청
        diaryStatusService.diaryStatus(
            token = accessToken,
            type = "public",
            value = (!(currentList[position].is_public)).toString(),
            diaryID = diaryId
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
                            // 공개 여부 전환 성공
                            currentList[position].is_public = !(currentList[position].is_public)
                            showResource(currentList[position].is_public, holder)
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                context.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "PUBLIC",
                                    intentData = null
                                )
                            }
                            // 삭제 실패 시 토스트 메시지 표시
                            showToast("다시 시도해주세요.")
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY PUBLIC] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                        // 삭제 실패 시 토스트 메시지 표시
                        showToast("다시 시도해주세요.")
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("DIARY", "[DIARY PUBLIC] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                    // 삭제 실패 시 토스트 메시지 표시
                    showToast("다시 시도해주세요.")
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
