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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyPageScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current

    // ViewModel들
    val myPageViewModel: MyPageViewModel = viewModel(factory = MyPageViewModelFactory(context))
    val resViewModel: RestaurantViewModel = viewModel(factory = RestaurantViewModelFactory(context))

    val totalVisitedCount by myPageViewModel.totalVisitedCount.collectAsState()
    val badges by myPageViewModel.badges.collectAsState()
    val activeBadgeCount = badges.count { it.isCompleted }

    // 식당 데이터 및 방문 필터링
    val restaurants by resViewModel.restaurants.collectAsState()
    val visitedIds by resViewModel.visitedIds.collectAsState()
    val visitedRestaurants = remember(restaurants, visitedIds) {
        restaurants.filter { visitedIds.contains(it.id) }
    }

    var selectedBadge by remember { mutableStateOf<BadgeUiState?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        // 1. 상단 통계 카드
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF660033)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("맛집 도장깨기", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("나만의 맛집 탐방 기록", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text(totalVisitedCount.toString(), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            Text("총 방문 식당", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            ExpandableBadgeGrid(badges)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. [추가된 섹션] 방문 맛집 지도


        // 3. 뱃지 섹션 헤더
        stickyHeader {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = "나의 뱃지 (${activeBadgeCount})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp)
                )
            }
        }


        // 4. 뱃지 그리드 리스트
        val sortedBadges = badges.sortedByDescending { it.isCompleted }
        val rows = sortedBadges.chunked(2)
        items(rows) { rowBadges ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowBadges.forEach { badge ->
                    Box(modifier = Modifier.weight(1f).clickable { selectedBadge = badge }) {
                        BadgeItem(badge = badge)
                    }
                }
                if (rowBadges.size < 2) Spacer(modifier = Modifier.weight(1f))
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // ... (앞부분 생략) ...
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

                                            LabelOptions.from(pos)

                                                .setStyles(styles)

//.setTexts(res.name)

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

        }
    }


    if (selectedBadge != null) {
        BadgeDetailDialog(badge = selectedBadge!!, onDismiss = { selectedBadge = null })
    }


}

// 하단 BadgeItem, MiniStatCard, ExpandableBadgeGrid 함수들은 기존과 동일하므로 생략하지 않고 포함하여 복붙 가능하게 하세요.
@Composable
fun BadgeItem(badge: BadgeUiState) {
    val isCompleted = badge.isCompleted
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isCompleted) Color.White else Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 2.dp else 0.dp),
        border = if (isCompleted) BorderStroke(1.dp, Color(0xFFFFB300)) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(if (isCompleted) Color(0xFFFFF3E0) else Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) getCategoryIcon(badge.categoryId) else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (isCompleted) Color(0xFFFFB300) else Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(badge.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isCompleted) Color.Black else Color.Gray, textAlign = TextAlign.Center)
            Text("${badge.visitedCount}/${badge.totalCount}", fontSize = 12.sp, color = if (isCompleted) Color(0xFF800020) else Color.Gray)
        }
    }
}

@Composable
fun MiniStatCard(badge: BadgeUiState, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.Transparent), modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(badge.visitedCount.toString(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(badge.categoryId, color = Color.White, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
fun ExpandableBadgeGrid(badges: List<BadgeUiState>) {
    var expanded by remember { mutableStateOf(false) }
    val columnCount = 2
    val sortedBadges = remember(badges) { badges.sortedByDescending { it.visitedCount } }
    val visibleBadges = if (expanded) sortedBadges else sortedBadges.take(columnCount)
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "")

    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth().heightIn(max = 2000.dp)
        ) {
            items(visibleBadges) { badge -> MiniStatCard(badge = badge) }
        }
        if (badges.size > columnCount) {
            IconButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth().height(32.dp)) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.rotate(rotationState), tint = Color.Gray)
            }
        }
    }
}