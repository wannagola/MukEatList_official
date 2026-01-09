package com.example.mukeatlist.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MyPageScreen(paddingValues: PaddingValues) {
    Text(
        text = "마이페이지 (준비중)",
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    )
}
