package com.example.mukeatlist.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mukeatlist.R

@Composable
fun AppTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // 헤더 높이 (원하는 만큼 조절)
    ) {
        Image(
            painter = painterResource(R.drawable.logo_mukeatlist),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // "꽉 채우기"
        )
    }
}
