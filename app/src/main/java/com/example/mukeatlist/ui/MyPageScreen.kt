package com.example.mukeatlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukeatlist.viewmodel.BadgeUiState
import com.example.mukeatlist.viewmodel.MyPageViewModel
import com.example.mukeatlist.viewmodel.MyPageViewModelFactory

@Composable
fun MyPageScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val viewModel: MyPageViewModel = viewModel(
        factory = MyPageViewModelFactory(context)
    )
    
    val totalVisitedCount by viewModel.totalVisitedCount.collectAsState()
    val badges by viewModel.badges.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // 1. Top Statistics Card (Burgundy)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF660033) // Deep Burgundy
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "내가 다녀간 맛집",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${totalVisitedCount}곳",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "나의 뱃지",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Badge Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(badges) { badge ->
                BadgeItem(badge = badge)
            }
        }
    }
}

@Composable
fun BadgeItem(badge: BadgeUiState) {
    // Active Colors
    val activeContainerColor = Color(0xFFFFF3E0) // Light Orange/Gold bg
    val activeIconColor = Color(0xFFFFB300) // Gold
    val activeTextColor = Color.Black

    // Inactive Colors (Gray)
    val inactiveContainerColor = Color(0xFFF5F5F5)
    val inactiveIconColor = Color.Gray
    val inactiveTextColor = Color.Gray

    val isCompleted = badge.isCompleted

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color.White else inactiveContainerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 2.dp else 0.dp
        ),
        border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) activeContainerColor else Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.Star else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (isCompleted) activeIconColor else inactiveIconColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = badge.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) activeTextColor else inactiveTextColor,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Progress (e.g., 5/5)
            Text(
                text = "${badge.visitedCount}/${badge.totalCount}",
                fontSize = 12.sp,
                color = if (isCompleted) Color(0xFF800020) else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}