package com.example.mukeatlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Brush

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // 2초 대기
        onTimeout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF880044), // 시작: 살짝 밝고 영롱한 자주색
                        Color(0xFF660033), // 중간: 원래 원하시던 베이스 색
                        Color(0xFF33001A)  // 끝: 아주 깊고 어두운 와인색 (무게감)
                    )
                )
            ),
        // [핵심 1] 가로축 가운데 정렬 (좌우 중앙)
        horizontalAlignment = Alignment.CenterHorizontally,
        // [핵심 2] 세로축 가운데 정렬 (상하 중앙)
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.mukeatlist_mainlogo_white),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "먹킷리스트",
            color = Color.White,
            fontSize = 50.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(R.font.mujinjang))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "나만의 맛집 도장깨기",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

    }
}
