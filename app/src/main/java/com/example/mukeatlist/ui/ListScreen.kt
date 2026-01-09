package com.example.mukeatlist.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ListScreen(paddingValues: PaddingValues) {
    Text(
        text = "맛집 리스트 (준비중)",
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    )
}
