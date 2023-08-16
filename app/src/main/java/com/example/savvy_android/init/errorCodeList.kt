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
    when (errorCode) {
        // Common
        2000 -> {       // JWT 토큰 입력 필요
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3000 -> {       // JWT 토큰 검증 실패
            // JWT 토큰 검증하는 곳이 LoginActivity가 아니라면 재로그인을 위해 로그인 화면으로 이동
            if (this !is LoginActivity) {
                val intent = Intent(this, LoginActivity::class.java)
                // 홈 화면으로 연결되면 이전에 존재하던 splash, login activity 종료
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        2200 -> {       // KAKAO 토큰을 입력 필요
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        4003 -> {       // JWT 토큰 생성 실패
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        // Request error
        2001, 2014 -> { // 이메일을 입력해주세요
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2002 -> {       // 이메일은 30자리 미만으로 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2003 -> {       // 이메일을 형식을 정확하게 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2004 -> {       // 비밀번호를 입력 해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2005 -> {       // 비밀번호는 6~20자리를 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2006 -> {       // 닉네임을 입력 해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2007 -> {       // 닉네임은 최대 6자리 입니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2008 -> {       // 소개글은 100자 이하로 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2009 -> {       // 이메일은 30자리 미만으로 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2010 -> {       // 이메일을 형식을 정확하게 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2011 -> {       // 비밀번호를 입력 해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2012 -> {       // userId를 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2013 -> {       // 해당 회원이 존재하지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2015 -> {       // 해당 이메일을 가진 회원이 존재하지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2016 -> {       // 유저 아이디 값을 확인해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2017 -> {       // 변경할 닉네임 값을 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2018 -> {       // 회원 상태값을 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2019 -> {       // plannerId를 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2020 -> {       // type값이 올바르지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2021 -> {       // 제목은 15자리 미만으로 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2022 -> {       // 시간표를 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2023 -> {       // 검색어를 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2024 -> {       // 검색어는 15자리 미만으로 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2101 -> {       // 제목은 25자리 미만으로 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2102 -> {       // diaryId를 입력해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2103 -> {       // 다이어리 작성자가 아닙니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2300 -> {       // reason값이 유효하지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2301 -> {       // 하나 이상의 신고 사유를 체크해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2302 -> {       // 신고사유 중 기타를 체크하지 않았습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        2303 -> {       // is_blocked값이 유효하지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        //Response error
        3001 -> {       // 중복된 이메일입니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3002 -> {       // 중복된 닉네임입니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3003 -> {       // 아이디가 잘못되었습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3004 -> {       // 비밀번호가 잘못 되었습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3005 -> {       // 비활성화된 계정입니다. 고객센터에 문의해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3006 -> {       // 탈퇴 된 계정입니다. 고객센터에 문의해주세요.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3007 -> {       // 해다 여행계획서가 존재하지 않습니다.
            // 비어있는 목록의 경우 여기에 해당
//            Log.e(
//                type,
//                "[$type $detailType]: ($errorCode) $message"
//            )
        }

        3008, 3010 -> {       // 해당 스크랩이 존재하지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3009 -> {       // 해당 시간표가 존재하지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3011 ->{      // 업로드 되지 않은 여행계획서입니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3012 ->{      // 본인의 여행계획서는 스크랩 할 수 없습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3014 -> {       // 검색기록이 존재하지 않습니다.

        }

        3101 -> {       // 해당 다이어리가 존재하지 않습니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3200 -> {       // 회원가입이 필요한 사용자
            val profileIntent = Intent(
                this,
                ProfileSettingActivity::class.java
            )
            profileIntent.putExtra("kakaoToken", intentData)
            profileIntent.putExtra("isMyPage", false)
            startActivity(profileIntent)
        }

        3201 -> {       // 이미 존재하는 사용자입니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        3300 -> {       // 이미 신고한 여행계획서입니다.
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        // Connection, Transaction 등의 서버 오류
        4000 -> {       // 데이터베이스 에러
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        4001 -> {       // 서버 에러
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
        }

        4002 -> {       // AXIOS 에러
            Log.e(
                type,
                "[$type $detailType]: ($errorCode) $message"
            )
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