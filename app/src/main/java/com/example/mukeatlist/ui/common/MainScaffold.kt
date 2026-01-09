package com.example.mukeatlist.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mukeatlist.BottomNavItem
import com.example.mukeatlist.ui.ListScreen
import com.example.mukeatlist.ui.MyPageScreen
import com.example.mukeatlist.ui.photofeed.PhotoFeedScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukeatlist.viewmodel.PhotoFeedViewModel

@Composable
fun MainScaffold() {
    // 현재 탭 상태 (기본: 사진 피드로 시작하고 싶으면 Feed)
    var currentRoute by remember { mutableStateOf(BottomNavItem.Feed.route) }

    val items = listOf(
        BottomNavItem.List,
        BottomNavItem.Feed,
        BottomNavItem.My
    )

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            BottomNavBar(
                items = items,
                currentRoute = currentRoute,
                onItemClick = { item -> currentRoute = item.route }
            )
        }
    ) { paddingValues ->
        when (currentRoute) {
            BottomNavItem.List.route -> ListScreen(paddingValues)
            BottomNavItem.Feed.route -> {
                val vm: PhotoFeedViewModel = viewModel()
                PhotoFeedScreen(
                    paddingValues = paddingValues,
                    vm = vm,
                    onAddClick = {}
                )
            }
            BottomNavItem.My.route -> MyPageScreen(paddingValues)
        }
    }
}
