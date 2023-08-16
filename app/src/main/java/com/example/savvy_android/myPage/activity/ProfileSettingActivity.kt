package com.example.savvy_android.myPage.activity

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityProfileSettingBinding
import com.example.savvy_android.init.MainActivity
import com.example.savvy_android.init.data.SignupRequest
import com.example.savvy_android.init.data.SignupResponse
import com.example.savvy_android.init.data.image.SingleImageResponse
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.init.service.SignupService
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import com.example.savvy_android.utils.LoadingDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern


class ProfileSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingBinding
    private var duplicateState = false  // 중복 여부
    private var introState = true   // 소개글 가능 여부
    private var signupState = false // 회원 가입 가능 여부
    private var isChange = false // 프로필 사진 변경 여부
    private var isMypage = true // 연결 페이지 구분 (true: 마이페이지, false: 회원가입)
    private var profileLocalUri = Uri.EMPTY // 프로필 사진 (local)
    private var profileServerUrl = "" // 프로필 사진 (sever)
    private lateinit var imageBody: MultipartBody.Part
    private lateinit var sharedPreferences: SharedPreferences
    private var isFinish = false
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // 처음 회원가입으로 연결인지, 마이페이지 연결인지 구분
        isMypage = intent.getBooleanExtra("isMyPage", true)
        if (isMypage) { // 마이페이지에서 연결 경우 (프로필 편집 경우)
            binding.profileEditMode.visibility = View.VISIBLE
            binding.profileSignupBtn.text = "저장하기"
        } else { // 로그인페이지에서 연결 경우 (회원가입의 경우)
            binding.profileEditMode.visibility = View.GONE
            binding.profileSignupBtn.text = "회원가입"
        }

        // 뒤로 가기 버튼 클릭 이벤트
        binding.profileArrowIv.setOnClickListener {
            finish()
        }

        // 프로필 사진 layout 클릭 시 이벤트 처리
        binding.profileImgLayout.setOnClickListener {
            selectGallery()
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
            nicknameCheck(binding.profileNameEdit.text.toString())
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
            profileCombineAPI()
        }
    }

    // 갤러리 권한 후 이미지 선택
    private fun selectGallery() {
        when {
            // API 33 이상인 경우 READ_MEDIA_IMAGES 사용
            Build.VERSION.SDK_INT >= 33 -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // 권한이 없는 경우 권한 요청
                    requestReadMediaImagesPermissionLauncher.launch(
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    )
                    return
                }
            }

            else -> {
                // API 32 이하인 경우 READ_EXTERNAL_STORAGE WRITE_EXTERNAL_STORAGE 사용
                val permissions = arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                if (ContextCompat.checkSelfPermission(
                        this,
                        permissions[0]
                    ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        permissions[1]
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // 권한이 없는 경우 권한 요청
                    ActivityCompat.requestPermissions(this, permissions, 1)
                    return
                }
            }
        }
        // 이미 권한이 있는 경우 이미지 선택 처리
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        imageResult.launch(intent)
    }

    // READ_MEDIA_IMAGES 권한 요청을 처리하기 위한 런처
    private val requestReadMediaImagesPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용된 경우에 대한 처리
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
                imageResult.launch(intent)
            } else {
                // 권한이 거부된 경우에 대한 처리
                // 예를 들어 권한이 거부되면 사용자에게 알림을 표시하거나 다른 조치를 취할 수 있습니다.
            }
        }

    // 갤러리에서 선택한 이미지 결과 가져오기
    private val imageResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 결과 코드 OK
        if (it.resultCode == RESULT_OK) {
            val uri = it.data?.data // 결과 값 저장
            uri?.let {
                val inputStream = contentResolver.openInputStream(uri)
                val displayName = getDisplayName(contentResolver, uri)
                val mimeType = contentResolver.getType(uri) ?: "image/*"

                inputStream?.use { input ->
                    val imageFile = File(cacheDir, displayName)
                    val outputStream = FileOutputStream(imageFile)
                    outputStream.use { output ->
                        input.copyTo(output)
                    }

                    val fileRequestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    imageBody =
                        MultipartBody.Part.createFormData("image", displayName, fileRequestBody)
                }
                // 선택한 이미지 profileImageIv에 보여주기
                Glide.with(this)
                    .load(uri)
                    .into(binding.profileImgIv)

                // 이미지 변경 여부 변경
                isChange = true
            }
        }
    }

    // ContentResolver를 사용하여 Uri의 파일 이름 가져오기
    private fun getDisplayName(contentResolver: ContentResolver, uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1 && it.moveToFirst()) {
                return it.getString(displayNameIndex)
            }
        }
        return uri.lastPathSegment ?: "image.jpg"
    }

    // 닉네임 중복 API
    private fun nicknameCheck(nickname: String) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val duplicateService = retrofit.create(SignupService::class.java)

        duplicateService.duplicate(nickname = nickname)
            .enqueue(object : Callback<ServerDefaultResponse> {
                override fun onResponse(
                    call: Call<ServerDefaultResponse>,
                    response: Response<ServerDefaultResponse>,
                ) {
                    if (response.isSuccessful) {
                        val serverResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (serverResponse?.isSuccess == true && serverResponse.code == 1000) {
                            Log.e("TEST", "정상")
                            duplicateState = true   // 중복 여부 확인 저장 변수
                        } else if (serverResponse?.code == 3201) {
                            Log.e("TEST", "비정상")
                            duplicateState = false  // 중복 여부 확인 저장 변수
                        } else {
                            // 응답 에러 코드 분류
                            serverResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "PROFILE",
                                    detailType = null,
                                    intentData = null
                                )
                            }
                        }
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
                    } else {
                        Log.e(
                            "PROFILE",
                            "[PROFILE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("PROFILE", "[PROFILE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
    }

    // 회원가입 API
    private fun signupAPI(signupService: SignupService, dialog: LoadingDialogFragment) {
        val kakaoToken = intent.getStringExtra("kakaoToken")
        if (kakaoToken != null) {
            val nickname = binding.profileNameEdit.text.toString()
            val picUrl = profileServerUrl
            val intro = binding.profileIntroEdit.text.toString()

            val signupRequest =
                SignupRequest(kakaoToken, picUrl, nickname, intro)

            // 회원가입 API 호출
            signupService.signup(signupRequest)
                .enqueue(object : Callback<SignupResponse> {
                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val signupResponse = response.body()
                            if (signupResponse?.isSuccess == true) {
                                val serverToken =
                                    signupResponse.result.token
                                Log.d(
                                    "SIGNUP",
                                    "[SIGNUP] 회원가입 성공 - 서버에서 받은 토큰: $serverToken"
                                )

                                // 서버 토큰, 닉네임을 SharedPreferences에 저장
                                val editor = sharedPreferences.edit()
                                editor.putString("SERVER_TOKEN_KEY", serverToken)
                                editor.putString("USER_NICKNAME", nickname)
                                editor.apply()

                                val intent =
                                    Intent(
                                        this@ProfileSettingActivity,
                                        MainActivity::class.java
                                    )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                signupResponse?.let {
                                    errorCodeList(
                                        errorCode = it.code,
                                        message = it.message,
                                        type = "SIGNUP",
                                        detailType = null,
                                        intentData = null
                                    )
                                }
                            }
                        } else {
                            Log.e(
                                "SIGNUP",
                                "[SIGNUP] API 호출 실패 - 응답 코드: ${response.code()}"
                            )
                        }

                        // 로딩 다이얼로그 실행 여부 판단
                        if (isLoading) {
                            dialog.dismiss()
                        } else {
                            isFinish = true
                        }
                    }

                    override fun onFailure(
                        call: Call<SignupResponse>,
                        t: Throwable,
                    ) {
                        Log.e(
                            "SIGNUP",
                            "[SIGNUP] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                        )

                        // 로딩 다이얼로그 실행 여부 판단
                        if (isLoading) {
                            dialog.dismiss()
                        } else {
                            isFinish = true
                        }
                    }
                })
        } else {
            Log.e("SIGNUP", "[SIGNUP] API 호출 실패 - AccessToken이 없습니다.")
        }
    }

    // 이미지 API와 회원가입 API 결합
    private fun profileCombineAPI() {
        var isFinish = false
        var isLoading = false
        val dialog = LoadingDialogFragment()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinish) {
                dialog.show(supportFragmentManager, "LoadingDialog")
                isLoading = true
            }
            isFinish = false
            isLoading = false
        }, 500)

        //서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val signupService = retrofit.create(SignupService::class.java)
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)

        if (isMypage) {
            // 프로필 편집 경우
        } else {
            // 회원 가입 경우
            if (isChange) {
                // 프로필 사진을 추가했을 때
                signupService.uploadProfile(imageBody)
                    .enqueue(object : Callback<SingleImageResponse> {
                        override fun onResponse(
                            call: Call<SingleImageResponse>,
                            response: Response<SingleImageResponse>,
                        ) {
                            if (response.isSuccessful) {
                                val uploadProfileResponse = response.body()
                                if (uploadProfileResponse?.isSuccess == true) {
                                    profileServerUrl = uploadProfileResponse.result.pic_url
                                    signupAPI(signupService, dialog)
                                }
                            } else {
                                Log.e(
                                    "SIGNUP",
                                    "[IMAGE] API 호출 실패 - 응답 코드: ${response.code()}"
                                )
                            }

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }

                        override fun onFailure(call: Call<SingleImageResponse>, t: Throwable) {
                            Log.e(
                                "SIGNUP",
                                "[IMAGE] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                            )

                            // 로딩 다이얼로그 실행 여부 판단
                            if (isLoading) {
                                dialog.dismiss()
                            } else {
                                isFinish = true
                            }
                        }
                    })
            } else {
                // 프로필 사진을 추가 안했을 경우
                signupAPI(signupService, dialog)
            }
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