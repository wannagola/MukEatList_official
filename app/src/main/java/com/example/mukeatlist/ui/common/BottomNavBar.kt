package com.example.mukeatlist.ui.common

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.mukeatlist.BottomNavItem
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.mukeatlist.R
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,       // 선 두께 (0.5.dp로 하면 더 얇게 가능)
            color = Color.LightGray // 선 색상 (연한 회색 추천)
        )

        NavigationBar(
            modifier = modifier
                .height(72.dp),
            containerColor = Color.White
        ) {
            items.forEach { item ->

                val selected = currentRoute == item.route

                NavigationBarItem(
                    selected = selected,
                    onClick = { onItemClick(item) },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontFamily = FontFamily(Font(R.font.mujinjang)),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.offset(y = (-6).dp)
                        )
                    }
                )
            }
        }
    }
}