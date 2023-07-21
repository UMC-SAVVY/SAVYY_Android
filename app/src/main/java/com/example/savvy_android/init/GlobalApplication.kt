package com.example.savvy_android.init

import android.app.Application
import com.example.savvy_android.R
import com.kakao.sdk.common.KakaoSdk

// Android SDK를 사용하기 위해서는 가장 먼저 네이티브 앱 키로 초기화
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // KaKao SDK  초기화
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
    }
}