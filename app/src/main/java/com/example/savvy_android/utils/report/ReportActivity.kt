package com.example.savvy_android.utils.report

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityReportBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.activity.DiaryDetailActivity
import com.example.savvy_android.home.fragment.HomeFragment
import com.example.savvy_android.init.MainActivity

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding

    private var besidesChecked: Boolean = false
    private var nicknameChecked: Boolean = false
    private var contentChecked: Boolean = false
    private var abuseChecked: Boolean = false
    private var blockChecked: Boolean = false
    private var besidesText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // arrowLeft 아이콘 클릭하면 뒤로가기
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        binding.reportCompletionBtn.setOnClickListener {
            if (isReportCompletionButtonEnabled()) {
                // 커스텀 Toast 메시지 생성
                val toastBinding = LayoutToastBinding.inflate(layoutInflater)
                if (blockChecked) {
                    toastBinding.toastMessage.text = "성공적으로 신고 및 차단이 완료되었습니다"

                    val toast = Toast(this)
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = toastBinding.root

                    toast.setGravity(Gravity.TOP, 0, 120)  //toast 위치 설정

                    toast.show()

                    finish()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    toastBinding.toastMessage.text = "성공적으로 신고가 완료되었습니다"

                    val toast = Toast(this)
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = toastBinding.root

                    toast.setGravity(Gravity.TOP, 0, 120)  //toast 위치 설정

                    toast.show()

                    finish()
                }
            }
        }

        binding.nicknameCheckBtn.setOnClickListener {
            nicknameChecked = !nicknameChecked
            if (nicknameChecked) {
                // 클릭 시 Drawable 변경
                binding.nicknameCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_main)
                binding.nicknameCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_main))
            } else {
                // 다시 클릭 시 원래의 Drawable로 복원
                binding.nicknameCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_gray)
                binding.nicknameCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_gray))
            }
            updateBlockVisibility()
            updateReportCompletionButton()
        }

        binding.contentCheckBtn.setOnClickListener {
            contentChecked = !contentChecked
            if (contentChecked) {
                // 클릭 시 Drawable 변경
                binding.contentCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_main)
                binding.contentCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_main))
            } else {
                // 다시 클릭 시 원래의 Drawable로 복원
                binding.contentCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_gray)
                binding.contentCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_gray))
            }
            updateBlockVisibility()
            updateReportCompletionButton()
        }

        binding.abuseCheckBtn.setOnClickListener {
            abuseChecked = !abuseChecked
            if (abuseChecked) {
                // 클릭 시 Drawable 변경
                binding.abuseCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_main)
                binding.abuseCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_main))
            } else {
                // 다시 클릭 시 원래의 Drawable로 복원
                binding.abuseCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_gray)
                binding.abuseCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_gray))
            }
            updateBlockVisibility()
            updateReportCompletionButton()
        }

        binding.besidesCheckBtn.setOnClickListener {
            besidesChecked = !besidesChecked
            if (besidesChecked) {
                // 클릭 시 Drawable 변경
                binding.besidesCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_main)
                binding.besidesCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_main))
                binding.besidesEdit.visibility = View.VISIBLE
                binding.line5.visibility = View.VISIBLE
            } else {
                // 다시 클릭 시 원래의 Drawable로 복원
                binding.besidesCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_gray)
                binding.besidesCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_gray))
                binding.besidesEdit.visibility = View.GONE
                binding.line5.visibility = View.GONE
            }
            updateBlockVisibility()
            updateReportCompletionButton()
        }

        binding.blockCheckBtn.setOnClickListener {
            blockChecked = !blockChecked
            if (blockChecked) {
                // 클릭 시 Drawable 변경
                binding.blockCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_main)
                binding.blockCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_main))
            } else {
                // 다시 클릭 시 원래의 Drawable로 복원
                binding.blockCircle.background = ContextCompat.getDrawable(this, R.drawable.border_circle_gray)
                binding.blockCheckmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark_gray))
            }
            updateBlockVisibility()
            updateReportCompletionButton()
        }


        binding.besidesEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트 변경 전 호출되는 메서드
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경이 일어날 때 호출되는 메서드
                besidesText = s?.toString()?.trim() ?: ""
                updateReportCompletionButton()
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경이 완료된 후 호출되는 메서드
            }
        })
    }

    private fun updateBlockVisibility() {
        if (nicknameChecked || contentChecked || abuseChecked || besidesText.isNotEmpty()) {
            binding.block.visibility = View.VISIBLE
        } else {
            binding.block.visibility = View.GONE
        }
    }

    private fun isReportCompletionButtonEnabled(): Boolean {
        val besidesEditText = binding.besidesEdit.text.toString().trim()

        // besidesCheckedBtn이 클릭된 상태이고, besidesEdit에 글자가 입력되었을 경우 true
        if (besidesChecked && besidesEditText.isNotEmpty()) {
            return true
        }

        // 다른 버튼들이 클릭된 상태인지 검사
        val otherButtonsChecked = nicknameChecked || contentChecked || abuseChecked

        return otherButtonsChecked && (!besidesChecked || besidesEditText.isNotEmpty())
    }

    private fun updateReportCompletionButton() {
        val textColorResId = if (isReportCompletionButtonEnabled()) R.color.main else R.color.icon
        binding.reportCompletionBtn.setTextColor(ContextCompat.getColor(this, textColorResId))
    }
}