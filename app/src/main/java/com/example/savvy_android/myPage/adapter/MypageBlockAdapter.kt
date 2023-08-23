package com.example.savvy_android.myPage.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemBlockBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.myPage.data.BlockReleaseResponse
import com.example.savvy_android.myPage.data.MyPageBlockResult
import com.example.savvy_android.myPage.service.BlockService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageBlockAdapter(
    private val context: Context,
    private var blockList: ArrayList<MyPageBlockResult>,
) :
    RecyclerView.Adapter<MypageBlockAdapter.MypageBlockViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class MypageBlockViewHolder(binding: ItemBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var name = binding.blockNameTv
        val cancel = binding.blocKCancel
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MypageBlockViewHolder {
        val binding =
            ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MypageBlockViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: MypageBlockViewHolder, position: Int) {
        // 계획서 연결 or 마이페이지 연결구분에 따라 삭제 레이아웃 시각화 설정
        holder.name.text = blockList[position].nickname
        holder.cancel.setOnClickListener {
            // 커스텀 Toast 메시지 생성
            val inflater = LayoutInflater.from(holder.itemView.context)
            val toastBinding = LayoutToastBinding.inflate(inflater)
            toastBinding.toastMessage.text = "성공적으로 차단이 해제되었습니다."

            val toast = Toast(holder.itemView.context)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastBinding.root

            toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정

            toast.show()
        }

        holder.itemView.setOnClickListener {
            releaseBlockAPI(blockList[position].blocked_user)
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = blockList.size

    // 데이터 추가
    fun addBlock(blockData: MyPageBlockResult) {
        blockList.add(blockData)
        notifyItemInserted(blockList.size)
    }

    fun clearList() {
        blockList.clear() // 데이터 리스트를 비움
        notifyDataSetChanged() // 어댑터에 변경 사항을 알려서 리사이클뷰를 갱신
    }

    // 차단 목록 API
    private fun releaseBlockAPI(userId: Int) {
        // 서버 주소
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        // 서버 주소
        val serverAddress = context.getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val blockService = retrofit.create(BlockService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        blockService.blockRelease(token = accessToken, userId = userId)
            .enqueue(object : Callback<BlockReleaseResponse> {
                override fun onResponse(
                    call: Call<BlockReleaseResponse>,
                    response: Response<BlockReleaseResponse>,
                ) {
                    if (response.isSuccessful) {
                        val blockResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (blockResponse?.isSuccess == true && blockResponse.code == 1000) {
                            showToast("차단 해제를 성공했습니다.")
                        } else {
                            // 응답 에러 코드 분류
                            blockResponse?.let {
                                context.errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "BLOCK",
                                    detailType = "RELEASE",
                                    intentData = null
                                )
                            }
                            showToast("차단 해제를 실패했습니다.")
                        }
                    } else {
                        Log.e(
                            "BLOCK",
                            "[BLOCK RELEASE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                        showToast("차단 해제를 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<BlockReleaseResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("BLOCK", "[BLOCK RELEASE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                    showToast("차단 해제를 실패했습니다.")
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
