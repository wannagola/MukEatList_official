package com.example.mukeatlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mukeatlist.data.model.Restaurant
import com.example.mukeatlist.viewmodel.RestaurantViewModel
import com.example.mukeatlist.viewmodel.RestaurantViewModelFactory

import com.example.mukeatlist.ui.common.RestaurantDetailDialog

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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF800020) // Burgundy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "카테고리를 선택해주세요",
                fontSize = 14.sp,
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
                    label = { Text(category.name) },
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
                RestaurantItem(
                    restaurant = restaurant,
                    isVisited = visitedIds.contains(restaurant.id),
                    onClick = { selectedRestaurant = restaurant },
                    onVisitToggle = { viewModel.toggleVisit(restaurant.id) }
                )
            }
        }
    }

    // Detail Modal
    if (selectedRestaurant != null) {
        val restaurant = selectedRestaurant!!
        RestaurantDetailDialog(
            restaurant = restaurant,
            isVisited = visitedIds.contains(restaurant.id),
            onVisitToggle = { viewModel.toggleVisit(restaurant.id) },
            onDismiss = { selectedRestaurant = null }
        )
    }
}