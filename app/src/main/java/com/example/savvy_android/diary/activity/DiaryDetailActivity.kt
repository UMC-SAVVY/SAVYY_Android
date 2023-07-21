package com.example.savvy_android.diary.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.R.drawable.ic_heart
import com.example.savvy_android.R.drawable.ic_heart_gray
import com.example.savvy_android.diary.adapter.DetailAdapter
import com.example.savvy_android.diary.data.DiaryDetailItemData
import com.example.savvy_android.databinding.ActivityDiaryDetailBinding
import com.example.savvy_android.utils.BottomSheetDialogFragment
import com.example.savvy_android.diary.dialog.DiaryDeleteDialogFragment
import com.example.savvy_android.diary.dialog.DiaryModifyDialogFragment
import com.example.savvy_android.plan.activity.PlanDetailVisitActivity
import com.example.savvy_android.utils.BottomSheetOtherDialogFragment
import com.example.savvy_android.utils.report.ReportActivity

class DiaryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryDetailBinding
    private lateinit var diaryViewAdapter: DetailAdapter
    private var diaryViewData = arrayListOf<DiaryDetailItemData>()
    private var isLike: Boolean = false
    private var isShowingBottomSheet: Boolean = true  // 아마 API 연동하면 삭제

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // arrowLeft 아이콘 클릭하면 저장하지 않고 여행 계획서 페이지로 돌아가기
        binding.diaryArrowBtn.setOnClickListener {
            finish()
        }

        // 여행계획서 보러가기
        binding.diaryShowPlan.setOnClickListener {
            val intent = Intent(this, PlanDetailVisitActivity::class.java)
            startActivity(intent)
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


        // 옵션에서 내가작성한/다른사람이 작성한 다이어리 구분은 API 연결 후에 진행

        // 옵션 관련 (내가 작성한 다이어리)
        val bottomSheet = BottomSheetDialogFragment()
        bottomSheet.setButtonClickListener(object :
            BottomSheetDialogFragment.OnButtonClickListener {
            override fun onDialogEditClicked() {
//                val intent = Intent(this@DiaryViewActivity,)
//                intent.putExtra("planName", binding.travelPlanViewTitleTv.text.toString())
//                startActivity(intent)

                val dialog = DiaryModifyDialogFragment()
                dialog.show(supportFragmentManager, "DiaryModifyDialog")
            }

            override fun onDialogDeleteClicked() {
                val dialog = DiaryDeleteDialogFragment()

                // 다이얼로그 버튼 클릭 이벤트 설정
                dialog.setButtonClickListener(object :
                    DiaryDeleteDialogFragment.OnButtonClickListener {
                    override fun onDialogPlanBtnOClicked() {
                        finish()
                    }

                    override fun onDialogPlanBtnXClicked() {
                    }
                })
                dialog.show(supportFragmentManager,"DiaryDeleteDialog")
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
            if (isShowingBottomSheet) {
                bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")
            } else {
                bottomSheetOther.show(supportFragmentManager, "BottomSheetOtherDialogFragment")
            }
            // Toggle the flag
            isShowingBottomSheet = !isShowingBottomSheet
        }


        // 댓글 레이아웃 클릭 이벤트
        binding.diaryCommentLayout.setOnClickListener {
            val intent = Intent(this, DiaryCommentActivity::class.java)
            startActivity(intent)
        }

        // 디이어리 제목
        binding.diaryTitleTv.text = "제목이에용"

        // 다이어리 태그
        binding.diaryTagTv.text = "#태그 #입니다 #이건 #API #받구 #할거임"

        // 다이어리 작성자 프로필 사진
        binding.diaryUserImg.setImageResource(R.drawable.ic_launcher_foreground)

        // 다이어리 작성자
        binding.diaryNameTv.text = "이름이라넹"

        // 다이어리 날짜
        binding.diaryDateTv.text = "2023.06.29"

        // Plan Data & Adapter
        diaryViewAdapter = DetailAdapter(diaryViewData)
        binding.diaryDescribeRecycle.adapter = diaryViewAdapter


        // 더미 데이터 나중에 삭제할 것!!!
        diaryViewAdapter.addPlan(
            DiaryDetailItemData(
                0,
                true,
                "첫번째",
                R.drawable.ic_launcher_background,
                true,
                "와이키키 비치",
                "~~~"
            )
        )
        diaryViewAdapter.addPlan(
            DiaryDetailItemData(
                1,
                false,
                "두번째",
                R.drawable.ic_launcher_background,
                false,
                "하남돼지집",
                "~~~"
            )
        )
        diaryViewAdapter.addPlan(
            DiaryDetailItemData(
                2,
                false,
                "3번째",
                R.drawable.ic_launcher_background,
                true,
                "와이키키",
                "~~~"
            )
        )
        diaryViewAdapter.addPlan(
            DiaryDetailItemData(
                3,
                true,
                "4번째",
                R.drawable.ic_launcher_background,
                true,
                "와이키키",
                "~~~"
            )
        )
    }
}
