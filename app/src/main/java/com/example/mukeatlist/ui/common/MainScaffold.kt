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

@Composable
fun MainScaffold() {
    // 현재 탭 상태 (기본: 사진 피드로 시작하고 싶으면 Feed)
    var currentRoute by remember { mutableStateOf(BottomNavItem.List.route) }

    val items = listOf(
        BottomNavItem.List,
        BottomNavItem.Feed,
        BottomNavItem.My
    )
    
    val context = LocalContext.current

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
        var selectedCategory by remember { mutableStateOf<String?>(null) }

        when (currentRoute) {
            BottomNavItem.List.route -> {
                // [핵심 로직] 변수 값에 따라 화면 교체
                if (selectedCategory == null) {
                    // A. 선택된 게 없으면 -> 카테고리 고르는 화면
                    CategorySelectionScreen(
                        paddingValues = paddingValues,
                        onCategoryClick = { clickedCategory ->
                            // 클릭하면 변수에 값 저장 -> 화면이 자동으로 바뀜!
                            selectedCategory = clickedCategory
                        }
                    )
                } else {
                    // B. 선택된 게 있으면 -> 맛집 리스트 화면

                    // [중요] 핸드폰 뒤로가기 버튼 눌렀을 때 처리
                    BackHandler {
                        selectedCategory = null // 다시 null로 만들면 목록으로 돌아감
                    }

                    ListScreen(
                        paddingValues = paddingValues,
                        categoryId = selectedCategory!!, // "한식" 등의 데이터 전달
                        onBackClick = { selectedCategory = null } // 뒤로가기 버튼용
                    )
                }
            }            BottomNavItem.Feed.route -> {
                val vm: PhotoFeedViewModel = viewModel(
                    factory = PhotoFeedViewModelFactory(context)
                )
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
