package com.example.mukeatlist.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mukeatlist.data.model.Restaurant
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.viewinterop.AndroidView
import com.kakao.vectormap.MapView
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.example.mukeatlist.R

@Composable
fun RestaurantDetailDialog(
    restaurant: Restaurant,
    isVisited: Boolean,
    onVisitToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            // Box를 사용하여 스크롤 영역과 고정 버튼을 겹치게 배치
            Box(modifier = Modifier.fillMaxSize()) {

                // 1. 스크롤 가능한 콘텐츠 영역
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(bottom = 70.dp) // 버튼 영역(약 70dp)만큼 하단 여백을 주어 내용이 가려지지 않게 함
                ) {
                    // --- 이미지 슬라이더 영역 ---
                    Box(modifier = Modifier.height(200.dp)) {
                        val allImages = remember(restaurant) {
                            listOf(restaurant.thumbnailUrl) + restaurant.photoUrls
                        }
                        var currentIndex by remember { mutableStateOf(0) }

                        AsyncImage(
                            model = allImages.getOrNull(currentIndex),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (allImages.size > 1) {
                            IconButton(
                                onClick = { currentIndex = (currentIndex - 1 + allImages.size) % allImages.size },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전", tint = Color.White)
                            }

                            IconButton(
                                onClick = { currentIndex = (currentIndex + 1) % allImages.size },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음", tint = Color.White)
                            }
                        }

                        if (isVisited) {
                            Surface(
                                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xCCFFFFFF)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("방문완료", style = MaterialTheme.typography.labelMedium, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // --- 상세 정보 텍스트 영역 ---
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(restaurant.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = onVisitToggle) {
                                Icon(
                                    imageVector = if (isVisited) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isVisited) Color(0xFFD32F2F) else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("메인 메뉴: ${restaurant.mainMenu}", style = MaterialTheme.typography.bodyLarge)
                        Text("주소: ${restaurant.address}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("태그", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            restaurant.tags.take(4).forEach { tag ->
                                Surface(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(4.dp)) {
                                    Text("#$tag", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("위치 보기", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        // --- 지도 영역 ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        ) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { context ->
                                    MapView(context).apply {
                                        start(object : MapLifeCycleCallback() {
                                            override fun onMapDestroy() {}
                                            override fun onMapError(error: Exception?) {}
                                        }, object : KakaoMapReadyCallback() {
                                            override fun onMapReady(kakaoMap: KakaoMap) {
                                                val restaurantPos = LatLng.from(restaurant.lat.toDouble(), restaurant.lng.toDouble())
                                                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(restaurantPos))
                                                val styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.map_pin)))
                                                kakaoMap.labelManager?.layer?.addLabel(LabelOptions.from(restaurantPos).setStyles(styles))
                                            }
                                        })
                                    }
                                }
                            )
                        }
                    }
                }

                // 2. 하단에 고정된 닫기 버튼 (Box 내부에 위치하여 콘텐츠 위에 고정됨)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter) // 하단 중앙 고정
                        .fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp // 상단 콘텐츠와 구분되도록 그림자 추가
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800020)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("닫기", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
