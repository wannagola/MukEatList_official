package com.example.mukeatlist

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // ✅ 이 로그가 Logcat에 찍히는지 꼭 확인하세요!
        android.util.Log.d("KakaoMap", "GlobalApplication onCreate 시작됨")

        val appKey = "2ab9e5ee85e329361227dc75c8062dce"
        KakaoSdk.init(this, appKey)
        com.kakao.vectormap.KakaoMapSdk.init(this, appKey)
    }
}

