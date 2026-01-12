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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Star
import com.example.mukeatlist.ui.common.BadgeDetailDialog
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mukeatlist.ui.common.getCategoryIcon

@OptIn(ExperimentalFoundationApi::class) // stickyHeader 사용을 위해 필수
@Composable
fun MyPageScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val viewModel: MyPageViewModel = viewModel(
        factory = MyPageViewModelFactory(context)
    )
    val totalVisitedCount by viewModel.totalVisitedCount.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val activeBadgeCount = badges.count { it.isCompleted }
    var selectedBadge by remember { mutableStateOf<BadgeUiState?>(null) }

    // [핵심 변경] Column 대신 LazyColumn 사용
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), // 전체 화면 패딩
        contentPadding = PaddingValues(16.dp) // 리스트 내부 여백
    ) {
        // ==========================================
        // 1. 상단 통계 카드 (item으로 넣어서 스크롤 되게 함)
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF660033)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
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
                    Spacer(modifier = Modifier.height(8.dp))

                    // 내부 미니 카드 및 확장형 그리드
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
                                .padding(top = 12.dp),
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
                            Spacer(modifier = Modifier.height(0.dp))

                            // 기존에 만드신 확장형 그리드 (그대로 사용)
                            ExpandableBadgeGrid(badges)
                        }
                    }
                }
            }

            // 카드와 헤더 사이 간격
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 2. 뱃지 섹션 헤더 (Sticky Header 적용!)
        // ==========================================
        stickyHeader {
            // [중요] 배경색(Surface)을 줘야 스크롤 올라가는 아이템이 비치지 않습니다.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background // 혹은 Color.White 등 배경색
            ) {
                Text(
                    text = "나의 뱃지 (${activeBadgeCount})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp)
                )
            }
        }

        // ==========================================
        // 3. 뱃지 그리드 리스트 (LazyColumn에서 Grid 흉내내기)
        // ==========================================
        // GridCells.Fixed(2)와 똑같은 효과를 내기 위해 데이터를 2개씩 묶습니다.
        val sortedBadges = badges.sortedByDescending { it.isCompleted }
        val rows = sortedBadges.chunked(2)

        items(rows) { rowBadges ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp), // 아래쪽 간격 (verticalArrangement 대체)
                horizontalArrangement = Arrangement.spacedBy(12.dp) // 좌우 간격
            ) {
                rowBadges.forEach { badge ->
                    // weight(1f)로 공간을 똑같이 나눔
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedBadge = badge }
                    ) {
                        BadgeItem(badge = badge)
                    }
                }

                // [중요] 홀수 개수일 때 빈 공간 채우기 (모양 깨짐 방지)
                if (rowBadges.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 리스트 바닥 여백 추가 (선택사항)
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    if (selectedBadge != null) {
        BadgeDetailDialog(
            badge = selectedBadge!!, // null 아님을 보장
            onDismiss = { selectedBadge = null } // 닫으면 다시 null로 만들어 숨김
        )
    }
}

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
                        // ▼ 여기서 함수를 호출해서 카테고리에 맞는 아이콘을 가져옵니다.
                        getCategoryIcon(badge.categoryId)
                    } else {
                        Icons.Filled.Lock
                    },
                    contentDescription = null,
                    tint = if (isCompleted) activeIconColor else inactiveIconColor,
                    modifier = Modifier.size(40.dp)
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

@Composable
fun ExpandableBadgeGrid(badges: List<BadgeUiState>) {
    // 1. 상태 변수 (false: 접힘, true: 펼쳐짐)
    var expanded by remember { mutableStateOf(false) }

    // 2. 한 줄에 몇 개씩 보여줄지 설정
    val columnCount = 2

    val sortedBadges = remember(badges) { // remember를 써서 불필요한 재정렬 방지
        badges.sortedByDescending { it.visitedCount }
    }
    // 3. [핵심 로직] 보여줄 데이터 필터링
    // expanded가 true면 전체, false면 첫 줄 개수(3개)만큼만 자름
    val visibleBadges = if (expanded) sortedBadges else sortedBadges.take(columnCount)

    // 4. 화살표 회전 애니메이션
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = "rotation"
    )

    // 전체를 감싸는 컬럼 (애니메이션 적용 대상)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize() // [마법] 내용물(그리드) 높이가 변할 때 부드럽게 늘어남
    ) {
        // 그리드 (데이터 개수에 따라 높이 자동 조절)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            //verticalArrangement = Arrangement.spacedBy(12.dp),

            // [중요] 그리드 자체의 스크롤을 막아야 합니다!
            // 그래야 카드가 늘어날 때 전체 화면(MyPageScreen)이 스크롤됩니다.
            userScrollEnabled = false,

            modifier = Modifier
                .fillMaxWidth()
                // 그리드 높이를 내용물에 딱 맞춤 (최대 높이 제한을 넉넉히 줌)
                .heightIn(max = 2000.dp)
        ) {
            items(visibleBadges) { badge ->
                MiniStatCard(badge = badge)
            }
        }

        Spacer(modifier = Modifier.height(0.dp))

        // 펼치기/접기 버튼 (뱃지가 3개보다 많을 때만 보임)
        if (badges.size > columnCount) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "더보기",
                        modifier = Modifier.rotate(rotationState),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}