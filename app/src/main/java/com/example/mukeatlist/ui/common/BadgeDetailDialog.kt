package com.example.mukeatlist.ui.common

// 1. 기본 UI 구성요소 (Modifier, 정렬, 색상, 단위)
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

// 2. 레이아웃 (Column, 여백, 크기 조절)
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

// 3. 배경 및 모양
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

// 4. 머티리얼 디자인 3 컴포넌트 (카드, 버튼, 텍스트, 아이콘 등)
// *를 쓰면 Card, Text, Button 등을 일일이 안 써도 돼서 편합니다.
import androidx.compose.material3.* // 5. 다이얼로그(팝업) 및 컴포즈 함수
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable

// 6. 데이터 모델 (Badge 클래스)
import com.example.mukeatlist.viewmodel.BadgeUiState
@Composable
fun BadgeDetailDialog(
    badge: BadgeUiState,
    onDismiss: () -> Unit
) {
    // 획득 여부에 따른 색상 설정
    val activeColor = Color(0xFFFFC107) // 황금색 (획득)
    val inactiveColor = Color.Gray      // 회색 (미획득)
    val displayColor = if (badge.isCompleted) activeColor else inactiveColor

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 큰 아이콘 표시
                Icon(
                    imageVector = getCategoryIcon(badge.categoryId), // 기존에 만든 함수 활용
                    contentDescription = null,
                    tint = displayColor,
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = displayColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. 뱃지 이름
                Text(
                    text = badge.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. 상태 표시 (획득함 / 미획득)
                Surface(
                    color = if (badge.isCompleted) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (badge.isCompleted) "획득 완료!" else "미획득",
                        color = if (badge.isCompleted) Color(0xFF2E7D32) else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. 설명 및 획득 조건
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "설명",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF800020)
                    )
                    Text(
                        text = badge.description, // 예: "맛있는 한식을 많이 드셨군요!"
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "획득 조건",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF800020)
                    )
                    Text(
                        // 예: "한식 카테고리 방문 5회 달성" (데이터에 criteria가 없다면 문자열 조합)
                        text = "${badge.categoryId} 맛집 ${badge.totalCount}회 방문",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. 닫기 버튼
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800020))
                ) {
                    Text("닫기", color = Color.White)
                }
            }
        }
    }
}