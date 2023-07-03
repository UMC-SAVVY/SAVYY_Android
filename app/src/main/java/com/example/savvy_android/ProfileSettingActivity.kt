package com.example.savvy_android

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumptech.glide.Glide
import com.example.savvy_android.databinding.ActivityProfileSettingBinding
import java.util.regex.Pattern


class ProfileSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingBinding
    private var duplicateState = false  // 중복 여부
    private var introState = true   // 소개글 가능 여부
    private var signupState = false // 회원 가입 가능 여부

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 프로필 사진 layout 클릭 시 이벤트 처리
        binding.profileImgLayout.setOnClickListener {
            // 갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        // 닉네임 EditText 한글만 입력 (source: 입력된 문자열)
        binding.profileNameEdit.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            val pattern = Pattern.compile("[ㄱ-ㅣ가-힣]+") // 한글 문자 정규식
            val matcher = pattern.matcher(source) // source 와 정규식 패턴 비교
            // source 에 정규식 패턴과 일치 하지 않는 경우, 빈 문자열("")를 반환
            if (!matcher.matches()) {
                return@InputFilter ""
            }
            // filter 함수의 반환 타입은 CharSequence?이며, 필터링된 문자열 또는 null 반환
            // null 반환 시, 입력된 문자열 필터링 하지 않고 그대로 유지
            null
        })

        // 닉네임 EditText 입력 변화 이벤트 처리
        binding.profileNameEdit.addTextChangedListener(object : TextWatcher {
            // 텍스트 입력 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // 텍스트 입력 중
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 문자열 길이에 맞춰 중복 버튼 활성화 여부 결정 & 버튼 배경 변경
                val isEnableState = binding.profileNameEdit.length() != 0
                binding.profileNameDuplicateBtn.isEnabled = isEnableState
                btnStateBackground(isEnableState, binding.profileNameDuplicateBtn)
            }

            // 텍스트 입력 후
            override fun afterTextChanged(p0: Editable?) {}
        })

        // 닉네임 중복 버튼 클릭 이벤트
        binding.profileNameDuplicateBtn.setOnClickListener {
            duplicateState = !duplicateState    // 중복 여부 확인 저장 변수
            signupState = duplicateState && introState  // 회원 가입 여부

            // 중복 여부에 따른 테두리 변경
            editTextStateBackground(duplicateState, binding.profileNameEdit)
            // 중복 여부에 따른 안내 메시지 변경
            textViewStateDescription(
                duplicateState,
                binding.profileNameDuplicateTv,
                "사용 가능한 닉네임입니다",
                "중복된 닉네임입니다",
                R.color.green,
                R.color.main
            )

            // 중복 여부에 따른 회원 가입 버튼 활성화 여부 & 배경 변경
            binding.profileSignupBtn.isEnabled = signupState
            btnStateBackground(signupState, binding.profileSignupBtn)
        }

        // 소개글 EditText 입력 변화 이벤트 처리
        binding.profileIntroEdit.addTextChangedListener(object : TextWatcher {
            // 텍스트 입력 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // 텍스트 입력 중
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val introLength = binding.profileIntroEdit.length()
                introState = introLength < 100 // 문자열 길이 허용 여부
                signupState = duplicateState && introState  // 회원 가입 여부

                // 소개글 문장 길이 textView 변경
                textViewStateDescription(
                    introState,
                    binding.profileIntroCountTv,
                    "${introLength}/100",
                    "${introLength}/100",
                    R.color.basic_gray,
                    R.color.main
                )

                // 소개글 길이에 따른 테두리 변경
                editTextStateBackground(introState, binding.profileIntroEdit)

                // 소개글 길이에 따른 회원 가입 버튼 활성화 여부 & 배경 변경
                binding.profileSignupBtn.isEnabled = signupState
                btnStateBackground(signupState, binding.profileSignupBtn)
            }

            // 텍스트 입력 후
            override fun afterTextChanged(p0: Editable?) {}
        })

        // 회원 가입 버튼 클릭 이벤트
        binding.profileSignupBtn.setOnClickListener {
            Toast.makeText(this, "회원 가입 클릭 성공", Toast.LENGTH_SHORT).show()

            // 임시 연결
            val intent = Intent(this, TravelPlanMakeActivity::class.java)
            startActivity(intent)
        }
    }

    // 갤러리에서 선택한 이미지 결과 가져오기
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 결과 코드 OK, 결과값 not null 일 때
        if (it.resultCode == RESULT_OK && it.data != null) {
            val uri = it.data!!.data    // 결과 값 저장

            // 선택한 이미지 profileImageIv에 보여주기
            Glide.with(this)
                .load(uri)
                .into(binding.profileImgIv)
        }
    }

    // 클릭 가능 여부에 따른 button 배경 변경 함수
    private fun btnStateBackground(able: Boolean, objects: AppCompatButton) {
        // 클릭 가능: true, 불가능: false
        if (able)
            objects.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.main))
        else
            objects.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_line))
    }

    // 상태 가능 여부에 따른 editText 테두리 변경 함수
    private fun editTextStateBackground(able: Boolean, objects: EditText) {
        // 가능: true, 불가능: false
        if (able)
            objects.setBackgroundResource(R.drawable.border_gray)
        else
            objects.setBackgroundResource(R.drawable.border_red)
    }

    // EditText 여부에 따른 안내 문구 변경 함수
    private fun textViewStateDescription(
        able: Boolean, objects: TextView,
        trueString: String, falseString: String,
        trueColor: Int, falseColor: Int,
    ) {
        // 가능(true), 불가능(false)에 따른 textView string, text color 변경
        if (able) {
            objects.text = trueString
            objects.setTextColor(
                ContextCompat.getColor(this, trueColor)
            )
        } else {
            objects.text = falseString
            objects.setTextColor(
                ContextCompat.getColor(this, falseColor)
            )
        }
    }

}