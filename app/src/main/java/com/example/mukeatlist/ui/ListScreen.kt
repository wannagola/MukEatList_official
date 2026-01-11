package com.example.mukeatlist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukeatlist.R
import com.example.mukeatlist.data.model.Restaurant
import com.example.mukeatlist.ui.common.RestaurantDetailDialog
import com.example.mukeatlist.viewmodel.RestaurantViewModel
import com.example.mukeatlist.viewmodel.RestaurantViewModelFactory
import androidx.compose.foundation.ExperimentalFoundationApi

// 1. 폰트 정의 (파일명 mujinjang 적용)
val MujinjangFont = FontFamily(
    Font(R.font.mujinjang, FontWeight.Normal),
    Font(R.font.mujinjang, FontWeight.Bold) // 볼드 파일이 따로 없다면 동일하게 지정
)

@OptIn(ExperimentalFoundationApi::class) // stickyHeader 사용을 위해 필수
@Composable
fun ListScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val viewModel: RestaurantViewModel = viewModel(
        factory = RestaurantViewModelFactory(context)
    )
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val restaurants by viewModel.restaurants.collectAsState()
    val visitedIds by viewModel.visitedIds.collectAsState()

    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }

    // [변경 1] 전체를 아우르는 LazyColumn 사용
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), // Scaffold 패딩 적용
        contentPadding = PaddingValues(bottom = 16.dp) // 리스트 하단 여백
    ) {
        // [변경 2] Header 영역 (스크롤하면 위로 사라짐)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "어떤 맛집을 찾으시나요?",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = MujinjangFont,
                    color = Color(0xFF800020)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "카테고리를 선택해주세요",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = MujinjangFont,
                    color = Color.Gray
                )
            }
        }

        // [변경 3] Sticky Header 영역 (상단에 고정됨)
        stickyHeader {
            // [중요] 배경색이 없으면 스크롤된 아이템이 뒤에 비칩니다.
            // Surface나 Box로 감싸서 배경색을 꼭 지정해주세요.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f) // 혹은 Color.White
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp), // 상하 여백 조금 추가
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = category.id == selectedCategoryId
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCategorySelected(category.id) },
                            label = {
                                Text(
                                    text = category.name,
                                    fontFamily = MujinjangFont
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF800020),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // [변경 4] Restaurant List 영역
        // LazyColumn 안에 또 LazyColumn을 넣지 않고, items를 바로 씁니다.
        items(restaurants) { restaurant ->
            RestaurantItem(
                restaurant = restaurant,
                isVisited = visitedIds.contains(restaurant.id),
                onClick = { selectedRestaurant = restaurant },
                onVisitToggle = { viewModel.toggleVisit(restaurant.id) }
            )
        }
    }

    // 다이얼로그 (기존과 동일)
    if (selectedRestaurant != null) {
        RestaurantDetailDialog(
            restaurant = selectedRestaurant!!,
            isVisited = visitedIds.contains(selectedRestaurant!!.id),
            onVisitToggle = { viewModel.toggleVisit(selectedRestaurant!!.id) },
            onDismiss = { selectedRestaurant = null }
        )
    }
}