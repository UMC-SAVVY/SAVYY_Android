package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityDiaryModify1Binding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.activity.DiaryMake3Activity.Companion.imageCount
import com.example.savvy_android.diary.adapter.Make3Adapter
import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryHashtag
import com.example.savvy_android.plan.activity.PlanDetailActivity
import kotlin.properties.Delegates

class DiaryModify1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryModify1Binding
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var make3Adapter: Make3Adapter
    private var diaryID by Delegates.notNull<Int>()
    private var diaryDetailData = arrayListOf<DiaryContent>()
    private var diaryHashtagData = arrayListOf<DiaryHashtag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryModify1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 0으로 초기화
        imageCount = 0

        // 다이어리 정보 받기
        diaryID = intent.getIntExtra("diaryID", -1)
        if (diaryID == -1) {
            showToast("다이어리 내용을 불러오지 못했습니다.")
            finish()
        }
        binding.titleEdit.setText(intent.getStringExtra("title").toString())
        diaryDetailData =
            intent.getParcelableArrayListExtra<DiaryContent>("diaryContent") as ArrayList<DiaryContent>
        diaryHashtagData =
            intent.getParcelableArrayListExtra<DiaryHashtag>("diaryHashtag") as ArrayList<DiaryHashtag>

        // 이미지 개수 파악
        for (item in diaryDetailData) {
            if (item.type == "image")
                imageCount++
        }

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //seek bar 애니메이션
        binding.seekBar.max = 4000
        valueAnimator = ValueAnimator.ofInt(0, 2000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }
        valueAnimator.start()

        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        //  다음 클릭 이벤트
        binding.diaryNextBtn.setOnClickListener {
            if (binding.titleEdit.text.toString().isNotEmpty() && diaryDetailData.isNotEmpty()) {
                val intent =
                    Intent(this@DiaryModify1Activity, DiaryModify2Activity::class.java)
                intent.putExtra("diaryID", diaryID)
                intent.putExtra("title", binding.titleEdit.text.toString())
                intent.putParcelableArrayListExtra("diaryContent", diaryDetailData)
                intent.putParcelableArrayListExtra("diaryHashtag", diaryHashtagData)
                startActivity(intent)
            } else if (binding.titleEdit.text.toString().isEmpty()) {
                showToast("제목이 작성되지 않았습니다")
            } else if (diaryDetailData.isEmpty()) {
                showToast("내용이 작성되지 않았습니다")
            }
        }

        //다이어리 내용 리사이클뷰 리스트
        binding.diaryDetailRecycleModify.itemAnimator = null
        make3Adapter = Make3Adapter(this, diaryDetailData)
        binding.diaryDetailRecycleModify.adapter = make3Adapter
        binding.diaryDetailRecycleModify.layoutManager = LinearLayoutManager(this)

        binding.diaryTextBtn.setOnClickListener {
            make3Adapter.addDiary(
                DiaryContent(
                    count = make3Adapter.itemCount,
                    type = "text",
                    content = "",
                    location = null,
                )
            )
        }

        binding.diaryPictureBtn.setOnClickListener {
            if (imageCount < 10) // 이미지 최대 10개
                selectGallery()
            else
                showToast("사진은 최대 10개까지 추가 할 수 있습니다.")
        }

        binding.diaryPlanBtn.setOnClickListener {
            val intent = Intent(this, PlanDetailActivity::class.java)
            startActivity(intent)
        }
    }

    // 갤러리 권한 후 이미지 선택
    private fun selectGallery() {
        val writePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = when {
            // API 33 이상인 경우 READ_MEDIA_IMAGES 사용
            Build.VERSION.SDK_INT >= 33 -> {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                )
            }

            else -> {
                // API 32 이하인 경우 READ_EXTERNAL_STORAGE 사용
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
        if (writePermission == PackageManager.PERMISSION_DENIED ||
            readPermission == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            addImage.launch(intent)
        }
    }

    // 갤러리에서 선택한 이미지 결과 가져오기
    private val addImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 결과 코드 OK
        if (it.resultCode == RESULT_OK) {
            imageCount++
            val uri = it.data?.data // 결과 값 저장
            uri?.let {
                make3Adapter.addDiary(
                    DiaryContent(
                        count = make3Adapter.itemCount,
                        type = "image",
                        content = uri.toString(),
                        location = null,
                    )
                )
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE)) {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            if (!rect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                view.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 토스트 메시지
    private fun showToast(message: String) {
        val toastBinding =
            LayoutToastBinding.inflate(LayoutInflater.from(this@DiaryModify1Activity))
        toastBinding.toastMessage.text = message
        val toast = Toast(this@DiaryModify1Activity)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}
