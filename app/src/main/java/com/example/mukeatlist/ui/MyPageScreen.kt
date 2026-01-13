package com.example.mukeatlist.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukeatlist.R
import com.example.mukeatlist.viewmodel.BadgeUiState
import com.example.mukeatlist.viewmodel.MyPageViewModel
import com.example.mukeatlist.viewmodel.MyPageViewModelFactory
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.mukeatlist.ui.common.BadgeDetailDialog
import androidx.compose.foundation.clickable
import com.example.mukeatlist.ui.common.getCategoryIcon
import androidx.compose.ui.viewinterop.AndroidView


// 카카오맵 관련 import
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.example.mukeatlist.viewmodel.RestaurantViewModel
import com.example.mukeatlist.viewmodel.RestaurantViewModelFactory

import androidx.compose.foundation.ExperimentalFoundationApi

// =========================================================
// 1. 메인 화면 (MyPageScreen)
// =========================================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyPageScreen(
    paddingValues: PaddingValues,
    onNavigateToStamp: (Map<String, Int>) -> Unit
) {
    val context = LocalContext.current

    // ViewModel들
    val myPageViewModel: MyPageViewModel = viewModel(factory = MyPageViewModelFactory(context))
    val resViewModel: RestaurantViewModel = viewModel(factory = RestaurantViewModelFactory(context))

    val totalVisitedCount by myPageViewModel.totalVisitedCount.collectAsState()
    val badges by myPageViewModel.badges.collectAsState()
    val viewModel: MyPageViewModel = viewModel(
        factory = MyPageViewModelFactory(context)
    )

    val totalVisitedCount by viewModel.totalVisitedCount.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val activeBadgeCount = badges.count { it.isCompleted }

    // 식당 데이터 및 방문 필터링
    val restaurants by resViewModel.restaurants.collectAsState()
    val visitedIds by resViewModel.visitedIds.collectAsState()
    val visitedRestaurants = remember(restaurants, visitedIds) {
        restaurants.filter { visitedIds.contains(it.id) }
    }

    var selectedBadge by remember { mutableStateOf<BadgeUiState?>(null) }

    // [핵심 로직] 카테고리별 대표 뱃지 3개 추출
    // 1. 획득한 것 중 가장 높은 티어(ID가 작은 것)
    // 2. 없으면 가장 낮은 티어(ID가 큰 것 = 브론즈)를 잠금 상태로
    val representativeBadges = remember(badges) {
        val categories = listOf("blackwhitechef", "michelin", "tzuyang")
        categories.mapNotNull { category ->
            val categoryBadges = badges.filter { it.categoryId == category }

            // 획득한 뱃지 중 ID가 가장 작은 것(최상위 티어) 찾기
            val bestBadge = categoryBadges.filter { it.isCompleted }.minByOrNull { it.id }

            // 획득한 게 없으면 ID가 가장 큰 것(브론즈) 선택
            bestBadge ?: categoryBadges.maxByOrNull { it.id }
        }
    }

    // 배경 그라데이션
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF660033), Color(0xFFB36685)),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        // -----------------------------------------------------
        // 1. 상단 통계 카드
        // -----------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(brush = gradientBrush)
                        .fillMaxWidth()
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

                                // 확장형 그리드 (기존 유지)
                                ExpandableBadgeGrid(badges)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // -----------------------------------------------------
        // 2. 뱃지 섹션 헤더 (Sticky Header)
        // -----------------------------------------------------
        stickyHeader {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // 스탬프 화면으로 이동 (데이터 변환)
                        val visitCounts = badges.associate { it.categoryId to (it.visitedCount ?: 0) }
                        onNavigateToStamp(visitCounts)
                    },
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "나의 뱃지 ($activeBadgeCount)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "스탬프 화면 이동",
                        tint = Color.Gray
                    )
                }
            }
        }

/*이거해해결해
        item {
            Text(
                text = "내가 정복한 맛집 지도",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            start(object : MapLifeCycleCallback() {
                                override fun onMapDestroy() {}
                                override fun onMapError(error: Exception?) {}
                            }, object : KakaoMapReadyCallback() {
                                override fun onMapReady(kakaoMap: KakaoMap) {
                                    val layer = kakaoMap.labelManager?.layer
                                    val styles = kakaoMap.labelManager?.addLabelStyles(
                                        LabelStyles.from(LabelStyle.from(R.drawable.map_pin))
                                    )
                                    visitedRestaurants.forEach { res ->
                                        val pos = LatLng.from(res.lat.toDouble(), res.lng.toDouble())
                                        layer?.addLabel(
                                            LabelOptions.from(pos).setStyles(styles)
                                        )
                                    }
                                    if (visitedRestaurants.isNotEmpty()) {
                                        val firstPos = LatLng.from(
                                            visitedRestaurants[0].lat.toDouble(),
                                            visitedRestaurants[0].lng.toDouble()
                                        )
                                        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(firstPos, 10))
                                    }
                                }
                            })
                        }
                    },
                    update = { /* 데이터 갱신 시 지도 업데이트가 필요하면 여기에 작성 */ }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        */
        // -----------------------------------------------------
        // 3. 대표 뱃지 3개 가로 배치 (수정됨)
        // -----------------------------------------------------
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // 간격 균등 분배
            ) {
                representativeBadges.forEach { badge ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(100.dp)
                            .clickable {
                                // 획득했을 때만 팝업
                                if (badge.isCompleted) {
                                    selectedBadge = badge
                                }
                            }
                    ) {
                        // 스탬프 아이콘 UI (StampItem 로직과 유사하게 구현)
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .border(2.dp, Color.Black, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    if (badge.isCompleted) getStampBrush(badge.id)
                                    else SolidColor(Color.LightGray)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (badge.isCompleted) {
                                Icon(
                                    painter = painterResource(id = getCategoryIcon(badge.categoryId)),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.fillMaxSize(0.75f) // 아이콘 확대
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.fillMaxSize(0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 하단 텍스트 처리
                        val categoryName = when (badge.categoryId) {
                            "blackwhitechef" -> "흑백요리사"
                            "tzuyang" -> "쯔양"
                            "michelin" -> "미슐랭"
                            else -> "맛집"
                        }

                        val tierName = if (badge.isCompleted) {
                            when (badge.id) {
                                in 1..3 -> "마스터"
                                in 4..6 -> "골드"
                                in 7..9 -> "실버"
                                else -> "브론즈"
                            }
                        } else {
                            ""
                        }

                        // 획득 시: "흑백요리사 마스터", 미획득 시: "흑백요리사"
                        Text(
                            text = if (badge.isCompleted) "$categoryName $tierName" else categoryName,
                            fontSize = 14.sp,
                            fontWeight = if (badge.isCompleted) FontWeight.Bold else FontWeight.Normal,
                            color = if (badge.isCompleted) Color.Black else Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // -----------------------------------------------------
    // 4. 뱃지 상세 팝업 (새로운 스타일 적용)
    // -----------------------------------------------------
    if (selectedBadge != null) {
        BadgeDetailDialog(
            badge = selectedBadge!!,
            onDismiss = { selectedBadge = null }
        )
    }


}

// =========================================================
// 2. 팝업 컴포넌트 (BadgeDetailDialog) - MyStampScreen 스타일 적용
// =========================================================
@Composable
fun BadgeDetailDialog(
    badge: BadgeUiState,
    onDismiss: () -> Unit
) {
    val categoryName = when (badge.categoryId) {
        "blackwhitechef" -> "흑백요리사"
        "tzuyang" -> "쯔양"
        "michelin" -> "미슐랭"
        else -> "맛집"
    }

    val tierName = when (badge.id) {
        in 1..3 -> "마스터"
        in 4..6 -> "골드"
        in 7..9 -> "실버"
        else -> "브론즈"
    }

    val description = when (badge.id) {
        1 -> "흑백요리사 코스 완주.\n이제 요리는 작품으로 보임."
        2 -> "미슐랭 지도 들고 다녀도 될 레벨.\n별 수집가 최종 인증."
        3 -> "쯔양 맛집 코스 정복 완료.\n이제 나는 먹짱!"
        4 -> "핫한 트렌드 식당은\n이제 내 손바닥 안!"
        5 -> "별의 이유를 아는 사람.\n미슐랭 믿고 가도 됨."
        6 -> "내가 위만 컸어도 쯔양과 같이\n먹방했쯔양~>.<"
        7 -> "이건 그냥 맛이 아님.\n흑백요리사의 철학을 먹는 중."
        8 -> "이 집은 별 줄 만하네?\n미슐랭 감이 오기 시작함."
        9 -> "쯔양이 왜 이 집을 갔는지 아는 사람.\n실패 없는 맛집 감각."
        10 -> "TV에서 보던 그 셰프!\n요즘 핫한 흑백요리사 맛집 입장."
        11 -> "별 하나의 세계에 입문!\n미슐랭이 뭐길래… 일단 가봄."
        12 -> "먹방 유튜브계의 메시!\n쯔양이 방문한 맛집에서 먹쯔양~"
        else -> "화이팅"
    }

    val conditionText = when (badge.id) {
        in 1..3 -> "누적 방문 10회 달성"
        in 4..6 -> "누적 방문 5회 달성"
        in 7..9 -> "누적 방문 2회 달성"
        else -> "첫 방문 달성"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 스탬프 아이콘
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, Color.Black, CircleShape)
                        .clip(CircleShape)
                        .background(
                            if (badge.isCompleted) getStampBrush(badge.id)
                            else SolidColor(Color.LightGray)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (badge.isCompleted) {
                        Icon(
                            painter = painterResource(id = getCategoryIcon(badge.categoryId)),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.75f)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.fillMaxSize(0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 제목
                Text(
                    text = "$categoryName $tierName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 설명
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // 조건 표시
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "획득 조건",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF660033)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = conditionText,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// =========================================================
// 3. 헬퍼 컴포넌트 및 함수
// =========================================================

@Composable
fun MiniStatCard(
    badge: BadgeUiState,
    modifier: Modifier = Modifier
) {
    // 카테고리 ID를 한글 이름으로 변환
    val categoryName = when (badge.categoryId) {
        "blackwhitechef" -> "흑백요리사"
        "michelin" -> "미슐랭"
        "tzuyang" -> "쯔양"
        else -> "맛집"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.0f)),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
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
                text = categoryName, // [변경] badge.categoryId 대신 한글 이름 사용
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ExpandableBadgeGrid(badges: List<BadgeUiState>) {
    var expanded by remember { mutableStateOf(false) }
    val columnCount = 2

    // [핵심 변경] 같은 카테고리끼리 하나로 합치기
    val mergedBadges = remember(badges) {
        badges
            // 1. 카테고리 ID로 그룹핑 (Map<String, List<BadgeUiState>> 생성)
            .groupBy { it.categoryId }
            .map { (categoryId, list) ->
                // 2. 각 그룹에서 대표 데이터 하나 생성
                // (방문 횟수는 모두 같으므로 첫 번째 뱃지의 정보를 가져오거나 max값을 씀)
                val representative = list.first()

                // 화면에 표시할 '합쳐진 뱃지' 객체 리턴
                representative.copy(
                    visitedCount = list.maxOf { it.visitedCount } // 혹시 모르니 가장 높은 방문 수 사용
                )
            }
            // 3. 방문 횟수 많은 순서대로 정렬
            .sortedByDescending { it.visitedCount }
    }

    // [변경] mergedBadges를 기준으로 보여줄 개수 계산
    val visibleBadges = if (expanded) mergedBadges else mergedBadges.take(columnCount)

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = "rotation"
    )

    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            // verticalArrangement = Arrangement.spacedBy(12.dp), // 필요시 주석 해제
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth().heightIn(max = 2000.dp)
        ) {
            // [변경] visibleBadges 사용
            items(visibleBadges) { badge ->
                MiniStatCard(badge = badge)
            }
        }

        Spacer(modifier = Modifier.height(0.dp))

        // [변경] 합쳐진 개수(3개)가 컬럼 수(2개)보다 많을 때만 더보기 버튼 표시
        if (mergedBadges.size > columnCount) {
            Box(
                modifier = Modifier.fillMaxWidth().height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "더보기",
                        modifier = Modifier.rotate(rotationState),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
/*
// 색상 그라데이션 헬퍼
@Composable
fun getStampBrush(id: Int): Brush {
    val colors = when (id) {
        in 1..3 -> listOf(Color(0xFFE056FD), Color(0xFF686DE0))
        in 4..6 -> listOf(Color(0xFFFFD700), Color(0xFFDAA520))
        in 7..9 -> listOf(Color(0xFFE1E1E1), Color(0xFF7F8C8D))
        in 10..12 -> listOf(Color(0xFFCD7F32), Color(0xFF804A00))
        else -> listOf(Color.Gray, Color.DarkGray)
    }
    return Brush.verticalGradient(colors)
}

// 아이콘 리소스 헬퍼
@Composable
fun getCategoryIcon(categoryId: String): Int {
    return when (categoryId) {
        "tzuyang" -> R.drawable.youtube_icon
        "michelin" -> R.drawable.michelin_icon
        "blackwhitechef" -> R.drawable.blackwhitechef_icon
        else -> android.R.drawable.btn_star_big_on
    }
}
*/