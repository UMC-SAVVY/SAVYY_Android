package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.example.savvy_android.databinding.ActivityDiaryStep3Binding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.adapter.Make3Adapter
import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.make_modify.DiaryMakeModifyResponse
import com.example.savvy_android.diary.data.make_modify.DiaryMakeRequest
import com.example.savvy_android.diary.dialog.DiarySaveDialogFragment
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.MainActivity
import com.example.savvy_android.init.data.image.MultipleImageResponse
import com.example.savvy_android.init.errorCodeList
import com.example.savvy_android.myPage.dialog.MypageWithdrawalDialogFragment
import com.example.savvy_android.plan.activity.PlanDetailActivity
import com.example.savvy_android.plan.data.remove.ServerDefaultResponse
import com.example.savvy_android.plan.service.PlanListService
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

class DiaryMake3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep3Binding
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var make3Adapter: Make3Adapter
    private var diaryDetailData = arrayListOf<DiaryContent>()
    private var isDiary: Boolean = true
    private var plannerId = -1
    private var imageFileList = arrayListOf<MultipartBody.Part>()
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        var imageCount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 0으로 초기화
        imageCount = 0

        // 시작된 fragment 정보 받기
        isDiary = intent.getBooleanExtra("isDiary", true)
        plannerId = intent.getIntExtra("planID", -1)

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

        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            // 작성2로 돌아갔을 때 이전에 작성2에서 작성했던 여행계획서 삭제
            planRemoveAPI(plannerId.toString())
            finish()
        }

        //  다음 클릭 이벤트
        binding.diaryNextBtn.setOnClickListener {
            if (binding.titleEdit.text.toString().isNotEmpty() && diaryDetailData.isNotEmpty()) {
                val intent =
                    Intent(this@DiaryMake3Activity, DiaryMake4Activity::class.java)
                intent.putExtra("isDiary", isDiary)
                intent.putExtra("title", binding.titleEdit.text.toString())
                intent.putExtra("planID", plannerId)
                intent.putParcelableArrayListExtra("diaryContent", diaryDetailData)
                startActivity(intent)
            } else if (binding.titleEdit.text.toString().isEmpty()) {
                showToast("제목이 작성되지 않았습니다")
            } else if (diaryDetailData.isEmpty()) {
                showToast("내용이 작성되지 않았습니다")
            }
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

        //다이어리 내용 리사이클뷰 리스트
        binding.diaryDetailRecycle.itemAnimator = null
        make3Adapter = Make3Adapter(this, diaryDetailData)
        binding.diaryDetailRecycle.adapter = make3Adapter
        binding.diaryDetailRecycle.layoutManager = LinearLayoutManager(this)

        // 글 추가 버튼 클릭 이벤트
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

        // 사진 추가 버튼 클릭 이벤트
        binding.diaryImageBtn.setOnClickListener {
            if (imageCount < 10) // 이미지 최대 10개
                selectGallery()
            else
                showToast("사진은 최대 10개까지 추가 할 수 있습니다.")
        }

        // 계획서 보기 버튼 클릭 이벤트
        binding.diaryPlanBtn.setOnClickListener {
            if (plannerId != -1) {
                val intent = Intent(this, PlanDetailActivity::class.java)
                intent.putExtra("planID", plannerId)
                intent.putExtra("visibleOption", false)
                startActivity(intent)
            } else {
                showToast("계획서 ID가 없습니다")
            }
        }
    }

    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiarySaveDialogFragment()

        dialog.setButtonClickListener(object :
            DiarySaveDialogFragment.OnButtonClickListener {
            override fun onDialogSaveBtnOClicked() {
                diaryMakeTempAPI()
            }

            override fun onDialogCancelBtnXClicked() {

            }
        })
        dialog.show(supportFragmentManager, "diarySaveDialog")
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
        addImage.launch(intent)
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
                addImage.launch(intent)
            } else {
                // 권한이 거부된 경우에 대한 처리
                // 예를 들어 권한이 거부되면 사용자에게 알림을 표시하거나 다른 조치를 취할 수 있습니다.
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

    // 이미지 전송 API와 다이어리 전송 API 결합 함수
    private fun diaryMakeTempAPI() {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        sharedPreferences =
            getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        val diaryService = retrofit.create(DiaryService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // 입력한 내용 중 이미지만 찾아서 파일화
        for (item in diaryDetailData) {
            if (item.type == "image") {
                val uri = Uri.parse(item.content)
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
                    val imageBody =
                        MultipartBody.Part.createFormData("image", displayName, fileRequestBody)
                    imageFileList.add(imageBody)
                }
            }
        }

        if (imageFileList.isNotEmpty()) {
            // 이미지 전송하고 이미지 서버 주소 response
            diaryService.diaryImage(token = accessToken, imageFileList = imageFileList)
                .enqueue(object : Callback<MultipleImageResponse> {
                    override fun onResponse(
                        call: Call<MultipleImageResponse>,
                        response: Response<MultipleImageResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val diaryImageResponse = response.body()
                            // 서버 응답 처리 로직 작성
                            if (diaryImageResponse?.isSuccess == true) {
                                // 수신 받은 서버 이미지 주소를 알맞게 다시 넣어줌
                                var imageCount = 0
                                for (item in diaryDetailData) {
                                    if (item.type == "image") {
                                        item.content = diaryImageResponse.result[imageCount].pic_url
                                        imageCount++
                                    }
                                }

                                // 다이어리 생성 API에 필요한 요청 내용 생성
                                val diaryMakeRequest = DiaryMakeRequest(
                                    title = if (binding.titleEdit.text.isEmpty()) "제목이 없습니다" else binding.titleEdit.text.toString(),
                                    planner_id = plannerId,
                                    is_public = false,
                                    is_temporary = true,
                                    content = diaryDetailData,
                                    hashtag = null,
                                )

                                // 뒤로가기 버튼 클릭으로 실행된 경우만, 다이어리 저장 API 실행
                                diaryMakeAPI(diaryService, accessToken, diaryMakeRequest)
                            } else {
                                // 응답 에러 코드 분류
                                diaryImageResponse?.let {
                                    errorCodeList(
                                        errorCode = it.code,
                                        message = it.message,
                                        type = "DIARY",
                                        detailType = "MAKE3 IMAGE",
                                        intentData = null
                                    )
                                }
                            }
                        } else {
                            Log.e(
                                "DIARY",
                                "[DIARY MAKE3 IMAGE] API 호출 실패 - 응답 코드: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<MultipleImageResponse>, t: Throwable) {
                        // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                        Log.e(
                            "DIARY",
                            "[DIARY MAKE3 IMAGE] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                        )
                    }
                })
        } else {
            Log.e("TEST", "내용:${binding.titleEdit.text}\n여부${binding.titleEdit.text.isEmpty()}")
            // 다이어리 생성 API에 필요한 요청 내용 생성
            val diaryMakeRequest = DiaryMakeRequest(
                title = if (binding.titleEdit.text.isEmpty()) "제목이 없습니다" else binding.titleEdit.text.toString(),
                planner_id = plannerId,
                is_public = false,
                is_temporary = true,
                content = diaryDetailData,
                hashtag = null,
            )
            diaryMakeAPI(diaryService, accessToken, diaryMakeRequest)
        }
    }

    // 다이어리 작성 API
    private fun diaryMakeAPI(
        diaryService: DiaryService,
        accessToken: String,
        diaryMakeRequest: DiaryMakeRequest,
    ) {
        // POST 요청
        diaryService.diaryMake(token = accessToken, diaryMakeRequest = diaryMakeRequest)
            .enqueue(object : Callback<DiaryMakeModifyResponse> {
                override fun onResponse(
                    call: Call<DiaryMakeModifyResponse>,
                    response: Response<DiaryMakeModifyResponse>,
                ) {
                    if (response.isSuccessful) {
                        val planResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (planResponse?.isSuccess == true) {
                            showToast("작성 중인 다이어리가 임시 저장되었습니다.")

                            // MainActivity로 이동하면서 실행된 Fragment로 이동
                            val intent =
                                Intent(this@DiaryMake3Activity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            if (isDiary)
                                intent.putExtra(
                                    "SHOW_DIARY_FRAGMENT",
                                    true
                                ) // DiaryFragment를 보여주도록 추가 데이터 전달
                            else
                                intent.putExtra(
                                    "SHOW_HOME_FRAGMENT",
                                    true
                                ) // HomeFragment를 보여주도록 추가 데이터 전달
                            startActivity(intent)

                            finish()
                        } else {
                            // 응답 에러 코드 분류
                            planResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "MAKE3",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY MAKE3] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<DiaryMakeModifyResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e(
                        "DIARY",
                        "[DIARY MAKE3] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                    )
                }
            })
    }

    // 작성3에서 "<" 버튼을 클릭해서 작성2로 돌아갔을 때 이전에 작성2에서 작성했던 여행계획서 삭제
    // 여행계획서 삭제 API
    private fun planRemoveAPI(planId: String) {
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!

        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // API interface instance 생성
        val planListService = retrofit.create(PlanListService::class.java)
        val accessToken = sharedPreferences.getString("SERVER_TOKEN_KEY", null)!!

        // Delete 요청
        planListService.planDelete(
            token = accessToken,
            plannerId = planId,
            plannerType = "0"
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
                            finish()
                        } else {
                            // 응답 에러 코드 분류
                            deleteResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "PLAN",
                                    detailType = "DELETE",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "PLAN",
                            "[PLAN DELETE] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e("PLAN", "[PLAN DELETE] API 호출 실패 - 네트워크 연결 실패: ${t.message}")
                }
            })
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
            LayoutToastBinding.inflate(LayoutInflater.from(this@DiaryMake3Activity))
        toastBinding.toastMessage.text = message
        val toast = Toast(this@DiaryMake3Activity)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}