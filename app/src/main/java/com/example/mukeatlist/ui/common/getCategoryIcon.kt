package com.example.mukeatlist.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.mukeatlist.R

@Composable
fun getCategoryIcon(categoryId: String): ImageVector {
    return when (categoryId) {
        "blackwhitechef" -> ImageVector.vectorResource(R.drawable.blackwhitechef_icon)
        "michelin" -> ImageVector.vectorResource(R.drawable.michelin_icon)
        "tzuyang" -> ImageVector.vectorResource(R.drawable.youtube_icon)
        else -> Icons.Default.Star    // 기본 아이콘 (나머지)
    }
}