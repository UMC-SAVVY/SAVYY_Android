package com.example.savvy_android.diary.activity

import android.annotation.SuppressLint
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

class DiaryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryDetailBinding
    private lateinit var diaryViewAdapter: DetailAdapter
    private var diaryViewData = arrayListOf<DiaryDetailItemData>()
    private var isLike: Boolean = false

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

        // 옵션 관련
        val bottomSheet = BottomSheetDialogFragment()
        bottomSheet.setButtonClickListener(object :
            BottomSheetDialogFragment.OnButtonClickListener {
            override fun onDialogEditClicked() {
//                val intent = Intent(this@DiaryViewActivity,)
//                intent.putExtra("planName", binding.travelPlanViewTitleTv.text.toString())
//                startActivity(intent)
            }

            override fun onDialogDeleteClicked() {
                val dialog = DiaryDeleteDialogFragment()

                // 다이얼로그 버튼 클릭 이벤트 설정
                dialog.setButtonClickListener(object :
                    DiaryDeleteDialogFragment.OnButtonClickListener {
                    override fun onDialogPlanBtnOClicked() {
                        // 삭제 API

                        finish()
                    }

                    override fun onDialogPlanBtnXClicked() {
                    }
                })
                dialog.show(supportFragmentManager,"DiaryDeleteDialog")
            }
        })


        //option 클릭하면 bottom sheet
        binding.diaryOptionBtn.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "BottomSheetDialogFragment")

        }

        // 댓글 레이아웃 클릭 이벤트
        binding.diaryCommentLayout.setOnClickListener {
            // 댓글 화면으로 이동하는 레이아웃
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
        diaryViewAdapter.addDiary(
            DiaryDetailItemData(
                0,
                true,
                "첫번째",
                (R.drawable.ic_launcher_background).toString(),
                true,
                "와이키키 비치",
                "~~~"
            )
        )
        diaryViewAdapter.addDiary(
            DiaryDetailItemData(
                1,
                false,
                "두번째",
                (R.drawable.ic_launcher_background).toString(),
                false,
                "하남돼지집",
                "~~~"
            )
        )
        diaryViewAdapter.addDiary(
            DiaryDetailItemData(
                2,
                false,
                "3번째",
                (R.drawable.ic_launcher_background).toString(),
                true,
                "와이키키",
                "~~~"
            )
        )
        diaryViewAdapter.addDiary(
            DiaryDetailItemData(
                3,
                true,
                "4번째",
                (R.drawable.ic_launcher_background).toString(),
                true,
                "와이키키",
                "~~~"
            )
        )
    }
}
