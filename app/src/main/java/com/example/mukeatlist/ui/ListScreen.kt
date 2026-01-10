package com.example.mukeatlist.ui

import android.R.color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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


// 1. 폰트 정의 (파일명 mujinjang 적용)
val MujinjangFont = FontFamily(
    Font(R.font.mujinjang, FontWeight.Normal),
    Font(R.font.mujinjang, FontWeight.Bold) // 볼드 파일이 따로 없다면 동일하게 지정
)

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "어떤 맛집을 찾으시나요?",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MujinjangFont, // 적용
                color = Color(0xFF800020)

            )


            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "카테고리를 선택해주세요",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MujinjangFont, // 적용
                color = Color.Gray
            )
        }

        // Category Selector
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
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
                            fontFamily = MujinjangFont // 적용
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF800020),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Restaurant List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(restaurants) { restaurant ->
                // 주의: RestaurantItem 파일 내부의 Text들에도
                // 해당 파일에서 fontFamily = MujinjangFont를 똑같이 넣어주어야 합니다.
                RestaurantItem(
                    restaurant = restaurant,
                    isVisited = visitedIds.contains(restaurant.id),
                    onClick = { selectedRestaurant = restaurant },
                    onVisitToggle = { viewModel.toggleVisit(restaurant.id) }
                )
            }
        }
    }

    if (selectedRestaurant != null) {
        RestaurantDetailDialog(
            restaurant = selectedRestaurant!!,
            isVisited = visitedIds.contains(selectedRestaurant!!.id),
            onVisitToggle = { viewModel.toggleVisit(selectedRestaurant!!.id) },
            onDismiss = { selectedRestaurant = null }
        )
    }
}