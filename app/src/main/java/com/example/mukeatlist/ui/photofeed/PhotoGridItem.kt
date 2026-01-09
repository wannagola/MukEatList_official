package com.example.mukeatlist.ui.photofeed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PhotoCell(
    imageUrl: String,
    onClick: () -> Unit
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    )
}

@Composable
fun AddCell(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        tonalElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = "+", style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Composable
fun SkeletonCell() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp)),
        tonalElevation = 1.dp
    ) {}
}
