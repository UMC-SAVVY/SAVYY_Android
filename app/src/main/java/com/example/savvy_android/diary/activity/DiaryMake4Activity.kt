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
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savvy_android.R
import com.example.savvy_android.diary.adapter.Make4Adapter
import com.example.savvy_android.databinding.ActivityDiaryStep4Binding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.diary.data.detail.DiaryContent
import com.example.savvy_android.diary.data.detail.DiaryHashtag
import com.example.savvy_android.diary.data.make_modify.DiaryMakeRequest
import com.example.savvy_android.diary.data.make_modify.DiaryMakeModifyResponse
import com.example.savvy_android.diary.dialog.DiarySaveDialogFragment
import com.example.savvy_android.diary.service.DiaryService
import com.example.savvy_android.init.MainActivity
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

class DiaryMake4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryStep4Binding
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var diaryHashtagAdapter: Make4Adapter
    private var isDiary: Boolean = true
    private var title = ""
    private var plannerId = -1
    private var diaryDetailContent = arrayListOf<DiaryContent>()
    private var hashtagList = arrayListOf<DiaryHashtag>()
    private var imageFileList = arrayListOf<MultipartBody.Part>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // splash screen 설정, 관리 API 함수
        binding = ActivityDiaryStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작된 fragment 정보 받기
        isDiary = intent.getBooleanExtra("isDiary", true)
        this.title = intent.getStringExtra("title").toString()
        this.plannerId = intent.getIntExtra("planID", -1)
        this.diaryDetailContent =
            intent.getParcelableArrayListExtra<DiaryContent>("diaryContent") as ArrayList<DiaryContent>

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
            diaryMakeFinalAPI(diaryDetailContent, hashtagList)
        }

        // < 클릭 이벤트
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        // 하나의 text에서 특정 글자 색 바꾸기
        val spannableString = SpannableString(binding.diaryStep4Tv.text.toString())
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
        spannableString.setSpan(colorSpan, 75, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.diaryStep4Tv.text = spannableString
    }

    //뒤로가기 누르면 Dialog 띄우기
    override fun onBackPressed() {
        val dialog = DiarySaveDialogFragment(isDiary)
        dialog.show(supportFragmentManager, "diarySaveDialog")
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
    private fun diaryMakeFinalAPI(
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
            if (item.type == "image") {
                val imageFile = File(getPathFromUri(Uri.parse(item.content)))

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
            // 다이어리 생성 API에 필요한 요청 내용 생성
            val diaryMakeRequest = DiaryMakeRequest(
                title = title,
                planner_id = if (plannerId == -1) null else plannerId,
                is_public = false,
                is_temporary = false,
                content = diaryDetailContent,
                hashtag = hashtagList,
            )

            // 다이어리 작성 API 실행
            diaryMakeAPI(diaryService, accessToken, diaryMakeRequest)
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
                                var imageCount = 0
                                for (item in diaryDetailContent) {
                                    if (item.type == "image") {
                                        item.content = diaryImageResponse.result[imageCount].pic_url
                                        imageCount++
                                    }
                                }

                                // 다이어리 생성 API에 필요한 요청 내용 생성
                                val diaryMakeRequest = DiaryMakeRequest(
                                    title = title,
                                    planner_id = if (plannerId == -1) null else plannerId,
                                    is_public = true,
                                    is_temporary = false,
                                    content = diaryDetailContent,
                                    hashtag = hashtagList,
                                )

                                // 다이어리 작성 API 실행
                                diaryMakeAPI(diaryService, accessToken, diaryMakeRequest)
                            } else {
                                // 응답 에러 코드 분류
                                diaryImageResponse?.let {
                                    errorCodeList(
                                        errorCode = it.code,
                                        message = it.message,
                                        type = "DIARY",
                                        detailType = "MAKE4 IMAGE",
                                        intentData = null
                                    )
                                }
                                showToast("다이어리 작성을 실패했습니다")
                            }
                        } else {
                            Log.e(
                                "DIARY",
                                "[DIARY MAKE4 IMAGE] API 호출 실패 - 응답 코드: ${response.code()}"
                            )
                            showToast("다이어리 작성을 실패했습니다")
                        }
                    }

                    override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                        // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                        Log.e(
                            "DIARY",
                            "[DIARY MAKE4 IMAGE] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                        )
                        showToast("다이어리 작성을 실패했습니다")
                    }
                })
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
                            showToast("성공적으로 다이어리 작성이 완료되었습니다")

                            // MainActivity로 이동하면서 실행된 Fragment로 이동
                            val intent = Intent(this@DiaryMake4Activity, MainActivity::class.java)
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
                                    detailType = "MAKE4",
                                    intentData = null
                                )
                            }
                            showToast("다이어리 작성을 실패했습니다")
                        }
                    } else {
                        Log.e(
                            "DIARY",
                            "[DIARY MAKE4] API 호출 실패 - 응답 코드: ${response.code()}"
                        )
                        showToast("다이어리 작성을 실패했습니다")
                    }
                }

                override fun onFailure(call: Call<DiaryMakeModifyResponse>, t: Throwable) {
                    // 네트워크 연결 실패 등 호출 실패 시 처리 로직
                    Log.e(
                        "DIARY",
                        "[DIARY MAKE4] API 호출 실패 - 네트워크 연결 실패: ${t.message}"
                    )
                    showToast("다이어리 작성을 실패했습니다")
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