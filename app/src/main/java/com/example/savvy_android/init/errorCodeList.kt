package com.example.savvy_android.init

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.savvy_android.myPage.activity.ProfileSettingActivity

fun Context.errorCodeList(
    errorCode: Int,
    message: String,
    type: String,
    detailType: String?,
    intentData: String?,
): String? {
    Log.e(
        type,
        "[$type $detailType]: ($errorCode) $message"
    )
    when (errorCode) {
        // Common
        2000 -> {   // JWT 토큰 입력 필요
        }

        3000 -> {   // JWT 토큰 검증 실패
            // JWT 토큰 검증하는 곳이 LoginActivity가 아니라면 재로그인을 위해 로그인 화면으로 이동
            if (this !is LoginActivity) {
                val intent = Intent(this, LoginActivity::class.java)
                // 홈 화면으로 연결되면 이전에 존재하던 splash, login activity 종료
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        2200 -> {   // KAKAO 토큰을 입력 필요

        }

        4003 -> {

        }

        // Request error
        2001 -> {

        }

        2002 -> {

        }

        2003 -> {

        }

        2004 -> {

        }

        2005 -> {

        }

        2006 -> {

        }

        2007 -> {

        }

        2008 -> {

        }

        2009 -> {

        }

        2010 -> {

        }

        2011 -> {

        }

        2012 -> {

        }

        2013 -> {

        }

        2014 -> {

        }

        2015 -> {

        }

        2016 -> {

        }

        2017 -> {

        }

        2018 -> {

        }

        2019 -> {

        }

        2020 -> {

        }

        2021 -> {

        }

        2022 -> {

        }

        2023 -> {

        }

        2024 -> {

        }

        2101 -> {

        }

        2102 -> {

        }

        2103 -> {

        }

        //Response error
        3001 -> {

        }

        3002 -> {

        }

        3003 -> {

        }

        3004 -> {

        }

        3005 -> {

        }

        3006 -> {

        }

        3007 -> {

        }

        3008 -> {

        }

        3009 -> {

        }

        3101 -> {

        }

        3200 -> {   // 회원가입이 필요한 사용자
            val profileIntent = Intent(
                this,
                ProfileSettingActivity::class.java
            )
            profileIntent.putExtra("kakaoToken", intentData)
            profileIntent.putExtra("isMyPage", false)
            startActivity(profileIntent)
        }

        3201 -> {

        }

        // Connection, Transaction 등의 서버 오류
        4000 -> {

        }

        4001 -> {

        }

        4002 -> {

        }

        // 기타의 경우
        else -> {
            Log.e(
                type,
                "[$type $detailType] 알 수 없는 에러 코드: ($errorCode) $message"
            )
            return null
        }
    }
    return null
}