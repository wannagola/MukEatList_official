package com.example.mukeatlist.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mukeatlist.BottomNavItem
import com.example.mukeatlist.ui.CategorySelectionScreen
import com.example.mukeatlist.ui.MyPageScreen
import com.example.mukeatlist.ui.photofeed.PhotoFeedScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukeatlist.viewmodel.PhotoFeedViewModel

import androidx.compose.ui.platform.LocalContext
import com.example.mukeatlist.ui.CategorySelectionScreen
import com.example.mukeatlist.viewmodel.PhotoFeedViewModelFactory
import androidx.activity.compose.BackHandler
import com.example.mukeatlist.ui.ListScreen
import com.example.mukeatlist.ui.MyStampScreen


@Composable
fun MainScaffold() {
    val items = listOf(BottomNavItem.List, BottomNavItem.Feed, BottomNavItem.My)
    val context = LocalContext.current

    var currentRoute by remember { mutableStateOf(BottomNavItem.List.route) }

    // [List 탭용 상태]
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // [My 탭용 상태 - 추가됨]
    // 스탬프 화면을 보여줄지 여부
    var showStampScreen by remember { mutableStateOf(false) }
    // 스탬프 화면에 넘겨줄 데이터 (방문 횟수)
    var myStampData by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            BottomNavBar(
                items = items,
                currentRoute = currentRoute,
                onItemClick = { item ->
                    if (currentRoute == item.route) {
                        // 이미 선택된 탭을 다시 눌렀을 때 (초기화 로직)
                        if (item.route == BottomNavItem.List.route) {
                            selectedCategory = null
                        }
                        // [추가됨] My 탭을 다시 누르면 스탬프 화면 닫고 메인 MyPage로 복귀
                        if (item.route == BottomNavItem.My.route) {
                            showStampScreen = false
                        }
                    } else {
                        currentRoute = item.route
                    }
                }
            )
        }
    ) { paddingValues ->
        when (currentRoute) {
            BottomNavItem.List.route -> {
                if (selectedCategory == null) {
                    CategorySelectionScreen(
                        paddingValues = paddingValues,
                        onCategoryClick = { selectedCategory = it }
                    )
                } else {
                    BackHandler { selectedCategory = null }
                    ListScreen(
                        paddingValues = paddingValues,
                        categoryId = selectedCategory!!,
                        onBackClick = { selectedCategory = null }
                    )
                }
            }
            BottomNavItem.Feed.route -> {
                val vm: PhotoFeedViewModel = viewModel(factory = PhotoFeedViewModelFactory(context))
                PhotoFeedScreen(paddingValues = paddingValues, vm = vm, onAddClick = {})
            }
            BottomNavItem.My.route -> {
                // [핵심 변경] 상태에 따라 화면 교체 (MyPage <-> MyStamp)
                if (showStampScreen) {
                    // 1. 스탬프 화면 (MyStampScreen)

                    // 뒤로가기 누르면 MyPage로 복귀
                    BackHandler {
                        showStampScreen = false
                    }

                    MyStampScreen(
                        paddingValues = paddingValues,
                        visitCounts = myStampData,
                        onBackClick = { showStampScreen = false }
                    )

                } else {
                    // 2. 마이 페이지 (MyPageScreen)
                    MyPageScreen(
                        paddingValues = paddingValues,
                        onNavigateToStamp = { counts ->
                            // 콜백: 화살표 누르면 데이터 저장하고 화면 전환
                            myStampData = counts
                            showStampScreen = true
                        }
                    )
                }
            }
        }
    }
}