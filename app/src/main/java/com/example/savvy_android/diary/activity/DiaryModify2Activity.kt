package com.example.savvy_android.diary.activity

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityDiaryModify2Binding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.adapter.Make4Adapter
import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryHashtag
import com.example.savvy_android.diary.data.make_modify.DiaryMakeModifyResponse
import com.example.savvy_android.diary.data.make_modify.DiaryModifyRequest
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.data.image.UploadImageResponse
import com.example.savvy_android.init.errorCodeList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import kotlin.properties.Delegates

class DiaryModify2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryModify2Binding
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var diaryHashtagAdapter: Make4Adapter
    private var title = ""
    private var diaryID by Delegates.notNull<Int>()
    private var diaryDetailContent = arrayListOf<DiaryContent>()
    private var hashtagList = arrayListOf<DiaryHashtag>()
    private var imageFileList = arrayListOf<MultipartBody.Part>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryModify2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다이어리 정보 받기
        diaryID = intent.getIntExtra("diaryID", -1)
        if (diaryID == -1) {
            showToast("다이어리 내용을 불러오지 못했습니다.")
            finish()
        }
        this.title = intent.getStringExtra("title").toString()
        this.diaryDetailContent =
            intent.getParcelableArrayListExtra<DiaryContent>("diaryContent") as ArrayList<DiaryContent>
        this.hashtagList =
            intent.getParcelableArrayListExtra<DiaryHashtag>("diaryHashtag") as ArrayList<DiaryHashtag>

        // 배경 색 지정
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        //seek bar 애니메이션
        binding.seekBar.max = 4000
        valueAnimator = ValueAnimator.ofInt(3000, 4000)
        valueAnimator.duration = 800
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            binding.seekBar.progress = value
        }
        valueAnimator.start()

        // RecyclerView에 HashtagAdapter 설정
        diaryHashtagAdapter = Make4Adapter(this, hashtagList)
        binding.recyclerviewHashtag.adapter = diaryHashtagAdapter
        binding.recyclerviewHashtag.layoutManager = LinearLayoutManager(this)

        // add_hashtag_btn 클릭 시 새로운 해시태그 추가
        binding.addHashtagBtn.setOnClickListener {
            diaryHashtagAdapter.addHashtag(DiaryHashtag(tag = ""))
        }

        // 완료 버튼 클릭시
        binding.diaryNextBtn.setOnClickListener {
            diaryModifyAPI(diaryDetailContent, hashtagList)
        }

        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryModify2Tv.text.toString())
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 75, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryModify2Tv.text = spannableString
    }

    // 이미지 확장자 확인
    private fun getMimeType(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    // 이미지 local uri를 절대 경로로 변환
    private fun getPathFromUri(uri: Uri): String {
        val proj =
            arrayOf(MediaStore.Images.Media.DATA)
        val c = contentResolver.query(uri, proj, null, null, null)
        val index = c!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

        c.moveToFirst()

        return c.getString(index)
    }

    // 다이어리 최종 작성 API
    private fun diaryModifyAPI(
        diaryDetailContent: ArrayList<DiaryContent>,
        hashtagList: ArrayList<DiaryHashtag>,
    ) {
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
        for (item in diaryDetailContent) {
            if (item.type == "image" && !item.content.contains("http")) {
                val imageFile =
                    File(getPathFromUri(Uri.parse(item.content)))

                // MIME 타입을 따르기 위해 image/jpg로 변환하여 RequestBody 객체 생성
                val mimeType = getMimeType(Uri.parse(item.content)) ?: "image/*"
                val fileRequestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                // RequestBody로 Multipart.Part 객체 생성
                val imageBody =
                    MultipartBody.Part.createFormData("image", imageFile.name, fileRequestBody)
                imageFileList.add(imageBody)
            }
        }

        if (imageFileList.isEmpty()) {
            // 다이어리 수정 API에 필요한 요청 내용 생성
            val diaryModifyRequest = DiaryModifyRequest(
                title = title,
                diary_id = diaryID,
                content = diaryDetailContent,
                hashtag = hashtagList,
            )

            // 다이어리 작성 API 실행
            diaryModifyAPI(diaryService, accessToken, diaryModifyRequest)
        } else {
            // 이미지 전송하고 이미지 서버 주소 response
            diaryService.diaryImage(token = accessToken, imageFileList = imageFileList)
                .enqueue(object : Callback<UploadImageResponse> {
                    override fun onResponse(
                        call: Call<UploadImageResponse>,
                        response: Response<UploadImageResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val diaryImageResponse = response.body()
                            // 서버 응답 처리 로직 작성
                            if (diaryImageResponse?.isSuccess == true) {
                                // 수신 받은 서버 이미지 주소를 알맞게 다시 넣어줌
                                var tempCount = 0
                                for (item in diaryDetailContent) {
                                    if (item.type == "image" && !item.content.contains("http")) {
                                        item.content = diaryImageResponse.result[tempCount].pic_url
                                        tempCount++
                                    }
                                }

                                // 다이어리 수정 API에 필요한 요청 내용 생성
                                val diaryModifyRequest = DiaryModifyRequest(
                                    title = title,
                                    diary_id = diaryID,
                                    content = diaryDetailContent,
                                    hashtag = hashtagList,
                                )

                                // 다이어리 작성 API 실행
                                diaryModifyAPI(diaryService, accessToken, diaryModifyRequest)
                            } else {
                                // 응답 에러 코드 분류
                                diaryImageResponse?.let {
                                    errorCodeList(
                                        errorCode = it.code,
                                        message = it.message,
                                        type = "DIARY",
                                        detailType = "MODIFY IMAGE",
                                        intentData = null
                                    )
                                }
                            }
                        } else {
                            Log.e(
                                "DIARY",
                                "[DIARY MODIFY IMAGE] API 호출 실패 - 응답 코드: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                        // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                        Log.e(
                            "DIARY",
                            "[DIARY MODIFY IMAGE] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                        )
                    }
                })
        }
    }

    // 다이어리 작성 API
    private fun diaryModifyAPI(
        diaryService: DiaryService,
        accessToken: String,
        diaryModifyRequest: DiaryModifyRequest,
    ) {
        // POST 요청
        diaryService.diaryModify(token = accessToken, diaryModifyRequest = diaryModifyRequest)
            .enqueue(object : Callback<DiaryMakeModifyResponse> {
                override fun onResponse(
                    call: Call<DiaryMakeModifyResponse>,
                    response: Response<DiaryMakeModifyResponse>,
                ) {
                    if (response.isSuccessful) {
                        val planResponse = response.body()
                        // 서버 응답 처리 로직 작성
                        if (planResponse?.isSuccess == true) {
                            Log.e("TEST", "눌림")
                            // 수정하기 전 다이어리 상세보기로 이동하면서 실행된 activity 종료
                            val intent =
                                Intent(this@DiaryModify2Activity, DiaryDetailActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)

                            showToast("성공적으로 수정이 완료되었습니다")
                        } else {
                            // 응답 에러 코드 분류
                            planResponse?.let {
                                errorCodeList(
                                    errorCode = it.code,
                                    message = it.message,
                                    type = "DIARY",
                                    detailType = "MODIFY",
                                    intentData = null
                                )
                            }
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY MODIFY] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<DiaryMakeModifyResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e(
                        "DIARY",
                        "[DIARY MODIFY] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                    )
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
        val toastBinding = LayoutToastBinding.inflate(LayoutInflater.from(this))
        toastBinding.toastMessage.text = message
        val toast = Toast(this)
        toast.view = toastBinding.root
        toast.setGravity(Gravity.TOP, 0, 145)  //toast 위치 설정
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}