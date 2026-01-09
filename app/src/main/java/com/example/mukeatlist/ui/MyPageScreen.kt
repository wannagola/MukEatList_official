package com.example.mukeatlist.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mukeatlist.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color      // Color (색상)
import androidx.compose.ui.unit.sp            // sp (글자 크기 단위)
import androidx.compose.ui.text.font.FontWeight // FontWeight (글자 굵기)
import androidx.compose.foundation.background   // background (배경색 Modifier)
import androidx.compose.ui.graphics.Brush

@Composable
fun MyPageScreen(paddingValues: PaddingValues) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(scrollState), // Scaffold의 padding 적용
        horizontalAlignment = Alignment.CenterHorizontally, // 가로 중앙 정렬
        verticalArrangement = Arrangement.Center // 세로 중앙 정렬 (화면 한가운데)
    ) {
        // 1. 큰 박스 (Card)
        Card(
            modifier = Modifier
                .fillMaxWidth() // 가로 꽉 채우기
                .padding(16.dp), // 바깥 여백
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // 그림자 효과
            colors = CardDefaults.cardColors(containerColor = Color.White) // 박스 배경색
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize() // 카드 꽉 채우기
                    .background(
                        brush = Brush.linearGradient( // 대각선 그라디언트
                            colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5)) // 보라 -> 민트
                        )
                    )
            )
            // 2. 박스 내부 정렬 (세로로 쌓기 위해 Column 사용)
            Column(
                modifier = Modifier.padding(16.dp) // 박스 안쪽 내용물과의 여백
            ) {
                // 3. 박스 안의 글자
                Text(text = "맛집 도장깨기", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp)) // 간격 띄우기
                Text(text = "나만의 맛집 탐방 기록", fontSize = 16.sp)

                Card(
                    modifier = Modifier
                        .fillMaxWidth() // 가로 꽉 채우기
                        .padding(8.dp), // 바깥 여백
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // 그림자 효과
                    colors = CardDefaults.cardColors(containerColor = Color.Red) // 박스 배경색
                ){
                    Text(text = "맛집 도장깨기", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                // 4. 박스 안의 작은 박스들 (가로로 배치하려면 Row)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween // 사이 간격 벌리기
                ) {
                    // 작은 박스 1
                    Box(
                        modifier = Modifier
                            .size(80.dp) // 크기 80x80
                            .background(Color.LightGray) // 회색 배경
                    ) {
                        Text("작은박스1", modifier = Modifier.align(Alignment.Center))
                    }

                    // 작은 박스 2
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Yellow) // 노란 배경
                    ) {
                        Text("작은박스2", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
        // 2. XML 벡터 이미지 표시
        Image(
            painter = painterResource(id = R.drawable.cook_icon),
            contentDescription = "요리사 아이콘",
            modifier = Modifier.size(80.dp), // 크기 조절 (원하는 대로 변경)
            // colorFilter = ColorFilter.tint(Color.Black) // 색상을 코드로 바꾸고 싶다면 주석 해제
        )

        Spacer(modifier = Modifier.height(16.dp)) // 이미지와 텍스트 사이 간격

        Text(
            text = "마이페이지 (준비중)"
        )

        Spacer(modifier = Modifier.height(500.dp))

        Text("아래 숨겨진 텍스트")
    }
}
