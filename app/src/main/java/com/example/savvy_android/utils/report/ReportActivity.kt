package com.example.savvy_android.utils.report

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ActivityReportBinding
import com.example.savvy_android.databinding.LayoutToastBinding
import com.example.savvy_android.init.MainActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private lateinit var sharedPreferences: SharedPreferences // sharedPreferences 변수 정의
    private var planID: Int = 0
    private var diaryID: Int = 0
    private var commentID: Int = 0
    private var nestedCommentID: Int = 0



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
        sharedPreferences = getSharedPreferences("SAVVY_SHARED_PREFS", Context.MODE_PRIVATE)!!
        planID = intent.getIntExtra("planID", 0)
        diaryID = intent.getIntExtra("diaryID", 0)
        commentID = intent.getIntExtra("commentID", 0)
        nestedCommentID = intent.getIntExtra("nestedCommentID", 0)

        Log.d("ReportActivity", "planID: $planID")
        Log.d("ReportActivity", "diaryID: $diaryID")
        Log.d("ReportActivity", "commentID: $commentID")
        Log.d("ReportActivity", "nestedCommentID: $nestedCommentID")


        // arrowLeft 아이콘 클릭하면 뒤로가기
        binding.arrowLeftBtn.setOnClickListener {
            finish()
        }

        binding.reportCompletionBtn.setOnClickListener {

            if (planID != 0) {
                val reportRequest = PlanReportRequest(
                    planner_id = planID,
                    reason_1 = if (nicknameChecked) 1 else 0,
                    reason_2 = if (contentChecked) 1 else 0,
                    reason_3 = if (abuseChecked) 1 else 0,
                    reason_4 = if (besidesChecked) 1 else 0,
                    contents = besidesText,
                    is_blocked = if (blockChecked) 1 else 0
                )
                Log.d("planReport", "Request: $reportRequest")

                planReportAPI(reportRequest)
            } else if (diaryID != 0) {
                val reportRequest = DiaryReportRequest(
                    diary_id = diaryID,
                    reason_1 = if (nicknameChecked) 1 else 0,
                    reason_2 = if (contentChecked) 1 else 0,
                    reason_3 = if (abuseChecked) 1 else 0,
                    reason_4 = if (besidesChecked) 1 else 0,
                    contents = besidesText,
                    is_blocked = if (blockChecked) 1 else 0
                )
                Log.d("diaryReport", "Request: $reportRequest")

                diaryReportAPI(reportRequest)
            } else if (commentID != 0) {
                val reportRequest = CommentReportRequest(
                    comment_id = commentID,
                    reason_1 = if (nicknameChecked) 1 else 0,
                    reason_2 = if (contentChecked) 1 else 0,
                    reason_3 = if (abuseChecked) 1 else 0,
                    reason_4 = if (besidesChecked) 1 else 0,
                    contents = besidesText,
                    is_blocked = if (blockChecked) 1 else 0
                )
                Log.d("commentReport", "Request: $reportRequest")

                commentReportAPI(reportRequest)
            } else if (nestedCommentID != 0) {
                val reportRequest = NestedCommentReportRequest(
                    reply_id = nestedCommentID,
                    reason_1 = if (nicknameChecked) 1 else 0,
                    reason_2 = if (contentChecked) 1 else 0,
                    reason_3 = if (abuseChecked) 1 else 0,
                    reason_4 = if (besidesChecked) 1 else 0,
                    contents = besidesText,
                    is_blocked = if (blockChecked) 1 else 0
                )
                Log.d("nestedCommentReport", "Request: $reportRequest")

                nestedCommentReportAPI(reportRequest)
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
        if (nicknameChecked || contentChecked || abuseChecked || besidesChecked) {
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

    // 댓글&답글 신고 api를 호출할 때
    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }

    private fun planReportAPI(planReportRequest: PlanReportRequest) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val reportService = retrofit.create(PlanReportService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        reportService.planReport(serverToken, planReportRequest).enqueue(object :
            Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    val reportResponse = response.body()
                    val isSuccess = reportResponse?.isSuccess
                    val code = reportResponse?.code
                    val message = reportResponse?.message
                    if (reportResponse != null && reportResponse.isSuccess) {
                        // 전송 성공
                        Log.d("PlanReportActivity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        if (isReportCompletionButtonEnabled()) {
                            // 커스텀 Toast 메시지 생성
                            if (blockChecked) {
                                showToast("성공적으로 신고 및 차단이 완료되었습니다")

                                finish()

                                val intent = Intent(this@ReportActivity, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                showToast("성공적으로 신고가 완료되었습니다")

                                finish()
                            }
                        }

                        planID = planReportRequest.planner_id
                    } else {
                        // 전송 실패
                        Log.d("PlanReportActivity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                        showToast("$message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("PlanReportActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                // 통신 실패
                Log.d("PlanReportActivity", "통신 실패 - ${t.message}")
            }
        })
    }


    private fun diaryReportAPI(diaryReportRequest: DiaryReportRequest) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val reportService = retrofit.create(DiaryReportService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        // 서버에 데이터 전송
        reportService.diaryReport(serverToken, diaryReportRequest).enqueue(object :
            Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    val reportResponse = response.body()
                    val isSuccess = reportResponse?.isSuccess
                    val code = reportResponse?.code
                    val message = reportResponse?.message
                    if (reportResponse != null && reportResponse.isSuccess) {
                        // 전송 성공
                        Log.d("DiaryReportActivity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        if (isReportCompletionButtonEnabled()) {
                            // 커스텀 Toast 메시지 생성
                            if (blockChecked) {
                                showToast("성공적으로 신고 및 차단이 완료되었습니다")

                                finish()

                                val intent = Intent(this@ReportActivity, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                showToast("성공적으로 신고가 완료되었습니다")

                                finish()
                            }
                        }

                        diaryID = diaryReportRequest.diary_id
                    } else {
                        // 전송 실패
                        Log.d("DiaryReportActivity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                        showToast("$message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("DiaryReportActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                // 통신 실패
                Log.d("DiaryReportActivity", "통신 실패 - ${t.message}")
            }
        })
    }

    private fun commentReportAPI(commentReportRequest: CommentReportRequest) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .build()

        val reportService = retrofit.create(CommentReportService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        val type = "comment"

        // 서버에 데이터 전송
        reportService.commentReport(serverToken, type, commentReportRequest).enqueue(object :
            Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    val reportResponse = response.body()
                    val isSuccess = reportResponse?.isSuccess
                    val code = reportResponse?.code
                    val message = reportResponse?.message
                    if (reportResponse != null && reportResponse.isSuccess) {
                        // 전송 성공
                        Log.d("CommentReportActivity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        if (isReportCompletionButtonEnabled()) {
                            // 커스텀 Toast 메시지 생성
                            if (blockChecked) {
                                showToast("성공적으로 신고 및 차단이 완료되었습니다")

                                finish()

                                val intent = Intent(this@ReportActivity, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                showToast("성공적으로 신고가 완료되었습니다")

                                finish()
                            }
                        }

                        commentID = commentReportRequest.comment_id
                    } else {
                        // 전송 실패
                        Log.d("CommentReportActivity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                        showToast("$message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("CommentReportActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                // 통신 실패
                Log.d("CommentReportActivity", "통신 실패 - ${t.message}")
            }
        })
    }

    private fun nestedCommentReportAPI(nestedCommentReportRequest: NestedCommentReportRequest) {
        // 서버 주소
        val serverAddress = getString(R.string.serverAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .build()

        val reportService = retrofit.create(NestedCommentReportService::class.java)

        val serverToken = sharedPreferences.getString("SERVER_TOKEN_KEY", "")!!

        val type = "reply"

        // 서버에 데이터 전송
        reportService.nestedCommentReport(serverToken, type, nestedCommentReportRequest).enqueue(object :
            Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    val reportResponse = response.body()
                    val isSuccess = reportResponse?.isSuccess
                    val code = reportResponse?.code
                    val message = reportResponse?.message
                    if (reportResponse != null && reportResponse.isSuccess) {
                        // 전송 성공
                        Log.d("ReplyReportActivity", "API 연동 성공 - isSuccess: $isSuccess, code: $code, message: $message")

                        if (isReportCompletionButtonEnabled()) {
                            // 커스텀 Toast 메시지 생성
                            if (blockChecked) {
                                showToast("성공적으로 신고 및 차단이 완료되었습니다")

                                finish()

                                val intent = Intent(this@ReportActivity, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                showToast("성공적으로 신고가 완료되었습니다")

                                finish()
                            }
                        }

                        nestedCommentID = nestedCommentReportRequest.reply_id
                    } else {
                        // 전송 실패
                        Log.d("ReplyReportActivity", "API 연동 실패 - isSuccess: $isSuccess, code: $code, message: $message")
                        showToast("$message")
                    }
                } else {
                    // 서버 오류
                    val errorCode = response.code()
                    Log.d("ReplyReportActivity", "서버 오류 - $errorCode")
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                // 통신 실패
                Log.d("ReplyReportActivity", "통신 실패 - ${t.message}")
            }
        })
    }


    // 토스트 메시지 표시 함수 추가
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