package com.example.savvy_android.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.adapter.DiaryPhotoAdapter
import com.example.savvy_android.adapter.PhotoItem
import com.example.savvy_android.databinding.ActivityDiaryStep3Binding
import com.example.savvy_android.dialog.DiarySaveDialogFragment

class DiaryStep3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep3Binding
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var diaryPhotoAdapter: DiaryPhotoAdapter
    private val selectedPhotos = mutableListOf<PhotoItem>()



    private val SELECT_IMAGE_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //seek bar 애니메이션
        binding.seekBar.max = 4000

        valueAnimator = ValueAnimator.ofInt(2000, 3000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }

        valueAnimator.start()

        binding.diaryNextBtn.setOnClickListener {
            val intent = Intent(this, DiaryStep4Activity::class.java)
            startActivity(intent)
        }

        binding.diaryContentBtn.setOnClickListener {

        }

        binding.diaryPictureBtn.setOnClickListener {
            // 갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)

        }


        binding.diaryPlanBtn.setOnClickListener {
            val intent = Intent(this, TravelPlanViewActivity::class.java)
            startActivity(intent)
        }


        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }


        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep3Tv.text.toString())

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 34, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan2, 48, 57, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan3 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan3, 64, 75, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan4 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan4, 85, 97, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep3Tv.text = spannableString

        //다이어리 사진 리스트
        diaryPhotoAdapter = DiaryPhotoAdapter(selectedPhotos)
        binding.recyclerviewPicture.adapter = diaryPhotoAdapter
        binding.recyclerviewPicture.layoutManager = LinearLayoutManager(this)
    }

    // 갤러리에서 선택한 이미지 결과 가져오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.data
            uri?.let {
                val photoUrl = uri.toString()
                val photoItem = PhotoItem(photoUrl)
                selectedPhotos.add(photoItem)
                diaryPhotoAdapter.notifyDataSetChanged()
            }
        }
    }


    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiarySaveDialogFragment()
        dialog.show(supportFragmentManager, "diarySaveDialog")
    }

}