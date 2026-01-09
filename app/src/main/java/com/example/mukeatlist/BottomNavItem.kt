package com.example.mukeatlist

import androidx.annotation.DrawableRes

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int
) {

    object List : BottomNavItem(
        route = "list",
        label = "맛집 리스트",
        iconRes = R.drawable.list_icon
    )

    object Feed : BottomNavItem(
        route = "feed",
        label = "사진 피드",
        iconRes = android.R.drawable.ic_menu_gallery
    )

    object My : BottomNavItem(
        route = "my",
        label = "마이페이지",
        iconRes = R.drawable.my_icon
    )

    companion object {
        //val items = listOf(List, Feed, My)
    }
}
