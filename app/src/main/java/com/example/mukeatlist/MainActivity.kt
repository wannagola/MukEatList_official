package com.example.mukeatlist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mukeatlist.ui.common.MainScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.MapView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 키 해시 확인용 코드 (함수 안으로 이동됨)
        val keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash", "내 컴퓨터의 키 해시: $keyHash")

        setContent {
            MainScaffold()
        }
    }
}

@Composable
fun KakaoMapScreen(modifier: Modifier = Modifier) {
    // AndroidView는 기존 View(MapView)를 Compose에서 쓸 수 있게 해줍니다.
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // 여기서 실제 MapView 객체를 생성합니다.
            MapView(context)
        }
        //update = { mapView ->
            // 데이터가 변경되었을 때 지도를 업데이트하는 로직을 넣습니다. }
    )
}
