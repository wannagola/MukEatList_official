package com.example.mukeatlist.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mukeatlist.R
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider

@Composable
fun AppTopBar() {

    Column(
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF880044), // 시작: 살짝 밝고 영롱한 자주색
                            Color(0xFF660033), // 중간: 원래 원하시던 베이스 색
                            Color(0xFF33001A)  // 끝: 아주 깊고 어두운 와인색 (무게감)
                        )
                    )
                ), // (선택사항) 배경색 추가
            // [핵심] 내용물들을 세로축 중앙에 정렬
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "먹킷리스트", // 원하는 텍스트
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.mujinjang))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(R.drawable.mukeatlist_mainlogo_white),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight() // 높이(60dp)는 꽉 채우고
                    .wrapContentWidth() // 너비는 이미지 비율에 맞춤
                    .padding(8.dp), // 너무 꽉 차면 답답하니 여백 살짝
                contentScale = ContentScale.Fit // 이미지가 잘리지 않게 비율 유지하며 맞춤
            )
        }
        HorizontalDivider(
            thickness = 1.dp,       // 선 두께 (0.5.dp로 하면 더 얇게 가능)
            color = Color.DarkGray // 선 색상 (연한 회색 추천)
        )
    }
}
