package com.example.savvy_android.diary.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.R.drawable.ic_heart
import com.example.savvy_android.R.drawable.ic_heart_gray
import com.example.savvy_android.diary.adapter.DetailAdapter
import com.example.savvy_android.databinding.ActivityDiaryDetailBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryDetailResponse
import com.example.savvy_android.diary.data.detail.DiaryHashtag
import com.example.savvy_android.utils.BottomSheetDialogFragment
import com.example.savvy_android.diary.dialog.DiaryDeleteDialogFragment
import com.example.savvy_android.diary.dialog.DiaryModifyDialogFragment
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.plan.activity.PlanDetailVisitActivity
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import com.example.savvy_android.utils.BottomSheetOtherDialogFragment
import com.example.savvy_android.utils.LoadingDialogFragment
import com.example.savvy_android.utils.report.ReportActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiaryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryDetailBinding
    private lateinit var diaryViewAdapter: DetailAdapter
    private var diaryViewData = arrayListOf<DiaryContent>()
    private var diaryHashtagData = arrayListOf<DiaryHashtag>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nickname: String
    private var isMine: Boolean = true // 다이어리가 본인것인지 판단
    private var isLike: Boolean = false
    private var diaryID: Int = 0
    private var planID: Int? = null
    private val likePush = "up"
    private val likeCancel = "down"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        nickname = sharedPreferences.getString("USER_NICKNAME", null)!!

        // 상세보기 다이어리의 id
        diaryID = intent.getIntExtra("diaryID", 0)

        // arrowLeft 아이콘 클릭하면 저장하지 않고 여행 계획서 페이지로 돌아가기
        binding.diaryArrowBtn.setOnClickListener {
            finish()
        }

        // 좋아요 버튼 클릭 이벤트
        binding.diaryLikeBtn.setOnClickListener {
            if (isLike) {
                likeAPI(diaryId = diaryID, changeValue = likeCancel)
            } else {
                likeAPI(diaryId = diaryID, changeValue = likePush)
            }
        }

        // Plan Data & Adapter
        diaryViewAdapter = DetailAdapter(
            diaryViewData
        )
        binding.diaryDescribeRecycle.adapter = diaryViewAdapter
    }

    override fun onResume() {
        super.onResume()

        diaryViewAdapter.clearList()
        diaryDetailAPI(diaryID, binding)

        // 옵션 관련 (내가 작성한 다이어리)
        val bottomSheet = BottomSheetDialogFragment()
        bottomSheet.setButtonClickListener(object :
            BottomSheetDialogFragment.OnButtonClickListener {
            override fun onDialogEditClicked() {    // 수정하기 클릭 시
                val dialog = DiaryModifyDialogFragment(
                    diaryID,
                    binding.diaryTitleTv.text.toString(),
                    diaryViewData,
                    diaryHashtagData
                )
                dialog.show(supportFragmentManager, "DiaryModifyDialog")
            }

            override fun onDialogDeleteClicked() { // 삭제하기 클릭 시
                val dialog = DiaryDeleteDialogFragment()
                // 다이얼로그 버튼 클릭 이벤트 설정
                dialog.setButtonClickListener(object :
                    DiaryDeleteDialogFragment.OnButtonClickListener {
                    override fun onDialogBtnOClicked() {
                        diaryRemoveAPI(diaryId = diaryID.toString())
                    }

                    override fun onDialogBtnXClicked() {
                    }
                })
                dialog.show(supportFragmentManager, "DiaryDeleteDialog")
            }
        })


        // 옵션 관련 (다른사람이 작성한 다이어리)
        val bottomSheetOther = BottomSheetOtherDialogFragment()
        bottomSheetOther.setButtonClickListener(object :
            BottomSheetOtherDialogFragment.OnButtonClickListener {
            override fun onDialogReportClicked() {
                val intent = Intent(this@DiaryDetailActivity, ReportActivity::class.java)
                startActivity(intent)
            }
        })

        // Option 버튼 클릭 시 본인  bottom sheet 표시
        binding.diaryOptionBtn.setOnClickListener {
            if (isMine)
                bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")
            else
                bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")
        }


        // 댓글 레이아웃 클릭 이벤트
        binding.diaryCommentLayout.setOnClickListener {
            val intent = Intent(this, DiaryCommentActivity::class.java)
            intent.putExtra("diaryID", diaryID)
            startActivity(intent)
        }
    }


    // 여행계획서 상세보기 API
    private fun diaryDetailAPI(diaryId: Int, binding: ActivityDiaryDetailBinding) {
        var isFinish = false
        var isLoading = false
        val dialog = LoadingDialogFragment()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinish) {
                dialog.show(supportFragmentManager, "LoadingDialog")
                isLoading = true
            }
        }, 500)


        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val diaryService = retrofit.create(DiaryService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        diaryService.diaryDetail(token = accessToken, diaryId = diaryId.toString())
            .enqueue(object : Callback<DiaryDetailResponse> {
                override fun onResponse(
                    call: Call<DiaryDetailResponse>,
                    response: Response<DiaryDetailResponse>,
                ) {
                    if (response.isSuccessful) {
                        val detailResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (detailResponse?.isSuccess == true) {
                            val result = detailResponse.result

                            // 디이어리 제목
                            binding.diaryTitleTv.text = result.title

                            // 다이어리 작성자 프로필 사진
                            if (!result.pic_url.isNullOrEmpty())
                                Glide.with(this@DiaryDetailActivity)
                                    .load(result.pic_url)
                                    .into(binding.diaryUserImg)

                            // 다이어리 작성자
                            binding.diaryNameTv.text = result.nickname

                            // 다이어리 날짜
                            binding.diaryDateTv.text = result.updated_at

                            // 다이어리 태그
                            var diaryHashTag = ""
                            if (result.hashtag != null) {
                                for (hashtag in result.hashtag) {
                                    diaryHashTag += "# ${hashtag.tag} "
                                }
                                diaryHashtagData = result.hashtag
                            }
                            binding.diaryTagTv.text = diaryHashTag

                            // 좋아요 여부
                            isLike = result.isLiked
                            if (isLike) {
                                binding.diaryLikeBtn.setImageResource(ic_heart)
                            } else {
                                binding.diaryLikeBtn.setImageResource(ic_heart_gray)
                            }

                            // 다이어리 내용
                            if (result.content != null) {
                                for (content in result.content) {
                                    diaryViewAdapter.addDiary(
                                        DiaryContent(
                                            count = content.count,
                                            type = content.type,
                                            content = content.content,
                                            location = content.location,
                                        )
                                    )
                                }
                            }

                            // 좋아요 개수
                            binding.diaryLikeTv.text = result.likes_count.toString()

                            // 댓글 개수
                            binding.diaryCommentTv.text = result.comments_count.toString()

                            // 다이어리 작성자와 유저가 동일인인지 판단
                            isMine = nickname == result.nickname

                            // 다이어리와 연결된 계획서 연결
                            planID = result.planner_id

                            // 여행계획서 보러가기
                            if (planID != null) {
                                binding.diaryShowPlan.setOnClickListener {

                                    val intent = if (isMine) Intent(
                                        this@DiaryDetailActivity,
                                        PlanDetailActivity::class.java
                                    ) else Intent(
                                        this@DiaryDetailActivity,
                                        PlanDetailVisitActivity::class.java
                                    )
                                    intent.putExtra("planID", planID)
                                    startActivity(intent)
                                }
                            } else {
                                binding.diaryShowPlan.setOnClickListener {
                                    showToast("여행계획서가 없습니다")
                                }
                            }


                        } else {
                            // 응답 에러 코드 분류
                            detailResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "DETAIL",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY DETAIL] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                }

                override fun onFailure(call: Call<DiaryDetailResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("DIARY", "[DIARY DETAIL] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                    // 로딩 다이얼로그 실행 여부 판단
                    if (isLoading) {
                        dialog.dismiss()
                    } else {
                        isFinish = true
                    }
                }
            })
    }

    // 다이어리 삭제 API
    private fun diaryRemoveAPI(diaryId: String) {
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
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
                            // 삭제 성공 시 토스트 메시지 표시
                            showToast("성공적으로 다이어리가 삭제되었습니다.")
                            finish()
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                errorCodeList(
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

    private fun likeAPI(diaryId: Int, changeValue: String) {
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val diaryStatusService = retrofit.create(DiaryService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // 좋아요 변경 요청
        diaryStatusService.diaryStatus(
            token = accessToken,
            type = "like",
            value = changeValue,
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
                            // 좋아요 성공
                            if (isLike) {
                                binding.diaryLikeBtn.setImageResource(ic_heart_gray)
                                binding.diaryLikeTv.text =
                                    "${Integer.parseInt(binding.diaryLikeTv.text as String) - 1}"
                            } else {
                                binding.diaryLikeBtn.setImageResource(ic_heart)
                                binding.diaryLikeTv.text =
                                    "${Integer.parseInt(binding.diaryLikeTv.text as String) + 1}"
                            }
                            // !!!! 좋아요 변한 내용 서버 전송 코드 필요
                            isLike = !isLike
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "LIKE",
                                    intentData = null
                                )
                            }
                            // 삭제 실패 시 토스트 메시지 표시
                            showToast("다시 시도해주세요.")
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY LIKE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                        // 삭제 실패 시 토스트 메시지 표시
                        showToast("다시 시도해주세요.")
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("DIARY", "[DIARY LIKE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                    // 삭제 실패 시 토스트 메시지 표시
                    showToast("다시 시도해주세요.")
                }
            })
    }

    // 토스트 메시지 표시 함수 추가
    private fun showToast(message: String) {
        val toastBinding = LayoutToastBinding.inflate(LayoutInflater.from(this))
        toastBinding.toastMessage.text = message
        val toast = Toast(this)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}
