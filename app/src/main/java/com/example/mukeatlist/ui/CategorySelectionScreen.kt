package com.example.mukeatlist.ui

import com.example.mukeatlist.R // 본인 패키지명 R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place // 임시 이미지용 아이콘
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukeatlist.BottomNavItem
import com.example.mukeatlist.ui.common.AppTopBar
import com.example.mukeatlist.ui.common.BottomNavBar
import com.example.mukeatlist.ui.photofeed.PhotoFeedScreen
import com.example.mukeatlist.viewmodel.PhotoFeedViewModel
import com.example.mukeatlist.viewmodel.PhotoFeedViewModelFactory

// 1. 더미 데이터 모델 (실제 앱에서는 서버나 DB 데이터 사용)
data class CategoryItemData(
    val categoryId: String,
    val title: String,
    val description: String,
    val imageRes: Int // 실제론 이미지 URL 등을 사용
)

@Composable
fun CategorySelectionScreen(
    paddingValues: PaddingValues,
    onCategoryClick: (String) -> Unit
) {
    // 샘플 데이터 생성
    val categories = listOf(
        CategoryItemData("tzuyang","유튜버 쯔양", "대식 먹방의 여왕", R.drawable.tzuyang_thumbnail),
        CategoryItemData("michelin","미슐랭", "별의 품격", R.drawable.michelin_thumbnail),
        CategoryItemData("blackwhitechef","흑백요리사", "요리 전쟁의 현장", R.drawable.blackwhitechef_thumbnail)
    )
    val myCustomFont = FontFamily(
        Font(R.font.mujinjang) // 파일 이름
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
    ) {
        // ==========================================
        // 1. 고정 헤더 영역 (스크롤 안 됨)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Text(
                text = "어떤 맛집을 찾으시나요?",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold, // extrabold 적용
                color = Color(0xFF800020),
                textAlign = TextAlign.Center,
                fontFamily = myCustomFont
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "카테고리를 선택해주세요",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold, // bold 적용
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = myCustomFont
            )
        }

        // 구분선 (선택사항)
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        // ==========================================
        // 2. 스크롤 가능한 리스트 영역
        // ==========================================
        LazyColumn(
            modifier = Modifier.weight(1f), // 남은 공간을 모두 차지
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // 아이템 간 간격
        ) {
            items(categories) { category ->
                CategoryListItem(
                    data = category,
                    onClick = {
                        onCategoryClick(category.categoryId)
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryListItem(
    data: CategoryItemData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // 카드 높이 고정 (적절히 조절 가능)
            .clickable(onClick = onClick), // 클릭 가능하게 설정
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)), // 연한 회색 배경
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
        ) {
            // 1. 왼쪽 이미지 영역
            Box(
                modifier = Modifier
                    .width(100.dp) // 이미지 너비
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp)),// 이미지 없을 때 회색 배경
                contentAlignment = Alignment.Center,
            ) {
                // 실제 이미지(AsyncImage 등)가 들어갈 자리
                Image(
                    painter = painterResource(id = data.imageRes), // 여기서 Int를 변환
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // 이미지를 꽉 차게 자름 (선택사항)
                    modifier = Modifier.fillMaxSize() // 박스 크기에 맞춤
                )
            }

            // 2. 오른쪽 텍스트 설명 영역
            Column(
                modifier = Modifier
                    .weight(1f) // 남은 가로 공간 차지
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // <제목>
                Text(
                    text = data.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // <설명>
                Text(
                    text = data.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 1 // 길면 한 줄로 자름
                )

                Spacer(modifier = Modifier.height(8.dp))

                // "자세히 보기 >"
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "자세히 보기",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF800020) // 포인트 컬러 (자주색 등)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF800020)
                    )
                }
            }
        }
    }
}