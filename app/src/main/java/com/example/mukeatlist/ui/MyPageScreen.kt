package com.example.mukeatlist.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukeatlist.R
import com.example.mukeatlist.viewmodel.BadgeUiState
import com.example.mukeatlist.viewmodel.MyPageViewModel
import com.example.mukeatlist.viewmodel.MyPageViewModelFactory
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate

@Composable
fun MyPageScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val viewModel: MyPageViewModel = viewModel(
        factory = MyPageViewModelFactory(context)
    )

    val totalVisitedCount by viewModel.totalVisitedCount.collectAsState()
    val badges by viewModel.badges.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // ==========================================
        // 1. 상단 통계 카드 (통합됨)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            // 높이를 고정(.height(120.dp))하지 않고 내용물에 맞게 늘어나도록 제거했습니다.
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF660033) // Deep Burgundy 배경색
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp) // 카드 내부 여백
            ) {
                // 1-1. 총 방문 식당 텍스트
                Text(
                    text = "맛집 도장깨기",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "나만의 맛집 탐방 기록",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                // 1-2. 방문 횟수 (큰 숫자)


                Spacer(modifier = Modifier.height(24.dp))

                // 1-3. 하단 미니 카드들 (Row로 배치)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Text(
                            text = totalVisitedCount.toString(),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "총 방문 식당",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp) // 카드 사이 간격
                        ) {
                            badges.forEach { badge ->
                            MiniStatCard(
                                badge = badge,
                                modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 2. 뱃지 섹션 헤더
        // ==========================================
        Text(
            text = "나의 뱃지",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 3. 뱃지 그리드 리스트
        // ==========================================
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 한 줄에 3개씩
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize() // 남은 공간 채우기
        ) {
            items(badges) { badge ->
                BadgeItem(badge = badge)
            }
        }
    }
}

// ▼▼▼ 하위 컴포넌트들은 변경 없음 (그대로 사용) ▼▼▼

@Composable
fun BadgeItem(badge: BadgeUiState) {
    val activeContainerColor = Color(0xFFFFF3E0)
    val activeIconColor = Color(0xFFFFB300)
    val activeTextColor = Color.Black
    val inactiveContainerColor = Color(0xFFF5F5F5)
    val inactiveIconColor = Color.Gray
    val inactiveTextColor = Color.Gray
    val isCompleted = badge.isCompleted

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color.White else inactiveContainerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 2.dp else 0.dp
        ),
        border = if (isCompleted) BorderStroke(1.dp, Color(0xFFFFB300)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) activeContainerColor else Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) {
                        ImageVector.vectorResource(id = R.drawable.cook_icon)
                    } else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (isCompleted) activeIconColor else inactiveIconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = badge.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) activeTextColor else inactiveTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${badge.visitedCount}/${badge.totalCount}",
                fontSize = 12.sp,
                color = if (isCompleted) Color(0xFF800020) else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MiniStatCard(
    badge: BadgeUiState,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.0f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier // 부모 Row의 weight에 맞춰 꽉 차게
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp), // 내부 여백 조절
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = badge.visitedCount.toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = badge.categoryId,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1, // 글자 넘침 방지
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
/*
@Composable
fun ExpandableCard(
    title: String,
    // ▼ String 대신 "화면 그리는 함수"를 통째로 받겠다고 선언
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(), // 애니메이션 필수
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF660033))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 제목
            Text(
                text = title,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // ▼ 내용물이 펼쳐졌을 때, 전달받은 content()를 그대로 실행(그리기)합니다.
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // 여기가 마법이 일어나는 곳입니다!
                // String을 출력하는 게 아니라 넘겨받은 UI 덩어리를 그립니다.
                content()
            }

            // 화살표 버튼
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.rotate(rotationState)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}*/