package com.example.mukeatlist.ui.photofeed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mukeatlist.data.model.Restaurant
import com.example.mukeatlist.ui.common.RestaurantDetailDialog
import com.example.mukeatlist.viewmodel.PhotoFeedViewModel

@Composable
fun PhotoFeedScreen(
    paddingValues: PaddingValues,
    vm: PhotoFeedViewModel,
    onAddClick: () -> Unit
) {
    val photos by vm.photos.collectAsState()
    val visitedIds by vm.visitedIds.collectAsState()
    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 4.dp), // Slight padding for grid
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 첫 칸은 + 버튼 (기능 유지)
        item {
            AddCell(onClick = onAddClick)
        }

        items(photos, key = { it.id }) { item ->
            PhotoCell(
                imageUrl = item.imageUrl,
                isVisited = visitedIds.contains(item.restaurant.id),
                onClick = { selectedRestaurant = item.restaurant }
            )
        }
    }

    if (selectedRestaurant != null) {
        val restaurant = selectedRestaurant!!
        RestaurantDetailDialog(
            restaurant = restaurant,
            isVisited = visitedIds.contains(restaurant.id),
            onVisitToggle = { vm.toggleVisit(restaurant.id) },
            onDismiss = { selectedRestaurant = null }
        )
    }
}