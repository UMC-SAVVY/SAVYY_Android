package com.example.savvy_android.diary.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.R.drawable.ic_heart
import com.example.savvy_android.R.drawable.ic_heart_gray
import com.example.savvy_android.diary.adapter.DetailAdapter
import com.example.savvy_android.databinding.ActivityDiaryDetailBinding
import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryDetailResponse
import com.example.savvy_android.utils.BottomSheetDialogFragment
import com.example.savvy_android.diary.dialog.DiaryDeleteDialogFragment
import com.example.savvy_android.diary.dialog.DiaryModifyDialogFragment
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.plan.activity.PlanDetailVisitActivity
import com.example.savvy_android.utils.BottomSheetOtherDialogFragment
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
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nickname: String
    private var isMine: Boolean = true // 다이어리가 본인것인지 판단
    private var isLike: Boolean = false
    private var diaryID: Int = 0
    private var planID: Int? = null

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
        }

        // Plan Data & Adapter
        diaryViewAdapter = DetailAdapter(
            diaryViewData
        )
        binding.diaryDescribeRecycle.adapter = diaryViewAdapter

        diaryDetailAPI(diaryID, binding)
    }

    override fun onResume() {
        super.onResume()

        // 여행계획서 보러가기
        if (planID != null) {
            binding.diaryShowPlan.setOnClickListener {
                val intent = if (isMine) Intent(this, PlanDetailActivity::class.java) else Intent(
                    this,
                    PlanDetailVisitActivity::class.java
                )
                intent.putExtra("planID", planID)
                startActivity(intent)
            }
        }

        // 옵션에서 내가작성한/다른사람이 작성한 다이어리 구분은 API 연결 후에 진행

        // 옵션 관련 (내가 작성한 다이어리)
        val bottomSheet = BottomSheetDialogFragment()
        bottomSheet.setButtonClickListener(object :
            BottomSheetDialogFragment.OnButtonClickListener {
            override fun onDialogEditClicked() {    // 수정하기 클릭 시
//                val intent = Intent(this,DiaryModify1Activity::class.java)
//                intent.putExtra("planName", diaryID)
//                startActivity(intent)

//                여기에 modifyDialog에서 수정 클릭시 뜨는 다이얼로그에 diaryID 넘겨줘야함

                val dialog = DiaryModifyDialogFragment()
                dialog.show(supportFragmentManager, "DiaryModifyDialog")
            }

            override fun onDialogDeleteClicked() { // 삭제하기 클릭 시
                val dialog = DiaryDeleteDialogFragment()
                // 다이얼로그 버튼 클릭 이벤트 설정
                dialog.setButtonClickListener(object :
                    DiaryDeleteDialogFragment.OnButtonClickListener {
                    override fun onDialogBtnOClicked() {
                        finish()
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

        // API 연결 전 임시 연결
        // Option 버튼 클릭 시 번갈아가며 bottom sheet 표시
        binding.diaryOptionBtn.setOnClickListener {
            if (planID != null) {
                if (isMine)
                    bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")
                else
                    bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")
            }
        }


        // 댓글 레이아웃 클릭 이벤트
        binding.diaryCommentLayout.setOnClickListener {
            val intent = Intent(this, DiaryCommentActivity::class.java)
            startActivity(intent)
        }
    }

    // 여행계획서 상세보기 API
    private fun diaryDetailAPI(diaryId: Int, binding: ActivityDiaryDetailBinding) {
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
                                    diaryHashTag += "#${hashtag.tag} "
                                }
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
                }

                override fun onFailure(call: Call<DiaryDetailResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("DIARY", "[DIARY DETAIL] API 호출 실패 - 네트워크 연결 실패: ${t.message}")

                }
            })
    }
}
