package com.example.mukeatlist.ui.common

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
@Composable
fun RestaurantDetailDialog(
    restaurant: Restaurant,
    isVisited: Boolean,
    onVisitToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp), // 최대 높이 제한
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // ==========================================
                // 1. 이미지 슬라이더 영역 (헤더)
                // ==========================================
                Box(modifier = Modifier.height(200.dp)) {

                    // 1-1. 이미지 데이터 준비 (썸네일 + 전체 사진)
                    val allImages = remember(restaurant) {
                        listOf(restaurant.thumbnailUrl) + restaurant.photoUrls
                    }

                    // 1-2. 현재 인덱스 상태
                    var currentIndex by remember { mutableStateOf(0) }

                    // 1-3. 메인 이미지 표시
                    AsyncImage(
                        model = allImages.getOrNull(currentIndex),
                        contentDescription = "음식 사진 ${currentIndex + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 1-4. 화살표 네비게이션 (사진이 2장 이상일 때만)
                    if (allImages.size > 1) {
                        // < 왼쪽 화살표
                        IconButton(
                            onClick = {
                                currentIndex = (currentIndex - 1 + allImages.size) % allImages.size
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "이전",
                                tint = Color.White
                            )
                        }

                        // > 오른쪽 화살표
                        IconButton(
                            onClick = {
                                currentIndex = (currentIndex + 1) % allImages.size
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "다음",
                                tint = Color.White
                            )
                        }

                        // [추가 기능] 페이지 번호 표시 (예: 1/5) - 우측 하단
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${currentIndex + 1} / ${allImages.size}",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 1-5. 방문 완료 뱃지 (기존 코드 유지) - 위치를 좌측 상단 등으로 옮겨도 됨
                    // (현재는 TopEnd인데, 사진 넘기기 버튼과 겹칠 수 있어서 TopStart로 옮기는 것 추천)
                    if (isVisited) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart) // [변경 추천] 오른쪽은 화살표가 있으니 왼쪽으로 이동
                                .padding(12.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xCCFFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "방문완료",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 2. 상세 정보 영역 (기존과 동일)
                // ==========================================
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Visit Toggle Button
                        IconButton(onClick = onVisitToggle) {
                            Icon(
                                imageVector = if (isVisited) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = "방문 체크",
                                tint = if (isVisited) Color(0xFFD32F2F) else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "메인 메뉴: ${restaurant.mainMenu}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "주소: ${restaurant.address}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "태그",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // 태그 리스트
                        restaurant.tags.take(4).forEach { tag ->
                            Surface(
                                color = Color(0xFFEEEEEE),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800020))
                    ) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}