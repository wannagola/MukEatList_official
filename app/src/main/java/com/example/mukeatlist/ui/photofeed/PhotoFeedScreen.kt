package com.example.mukeatlist.ui.photofeed

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.mukeatlist.viewmodel.PhotoFeedViewModel

@Composable
fun PhotoFeedScreen(
    paddingValues: PaddingValues,
    vm: PhotoFeedViewModel,
    onAddClick: () -> Unit
) {
    val pagingItems = vm.photos.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()

    // 바닥 근처에서 다음 페이지 로딩 트리거
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 8
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            pagingItems.loadState.append.let {
                // Paging이 알아서 로딩하긴 하는데, derivedState로 바닥 감지용
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 첫 칸은 + 버튼
        item {
            AddCell(onClick = onAddClick)
        }

        // 사진들
        items(
            count = pagingItems.itemCount,
            key = { idx -> pagingItems[idx]?.id ?: idx }
        ) { idx ->
            val item = pagingItems[idx]
            if (item == null) {
                SkeletonCell()
            } else {
                PhotoCell(
                    imageUrl = item.imageUrl,
                    onClick = { /* 나중에 상세로 */ }
                )
            }
        }

        // 하단 로딩 인디케이터
        item {
            val appendState = pagingItems.loadState.append
            if (appendState is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}
