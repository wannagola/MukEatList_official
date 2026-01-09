package com.example.mukeatlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.mukeatlist.data.paging.PhotoPagingSource
import com.example.mukeatlist.data.repository.PhotoRepository

class PhotoFeedViewModel : ViewModel() {

    // ✅ 1) fetcher 람다를 먼저 만든다 (PagingSource가 원하는 타입)
    private val fetcher: suspend (Int, Int) -> List<com.example.mukeatlist.data.model.FeedPhoto> =
        { page, size ->
            // ✅ 2) Repository는 "fetcher"를 받아야 하니까, 여기서 주입해서 만든다
            val repository = PhotoRepository(fetcher = { p, s ->
                // 🔥 여기 부분은 “실제 네가 사진을 가져오는 로직”으로 바꿔야 함.
                // 지금은 무한재귀 방지 위해 아래처럼 직접 구현해야 함.
                // (아래에 바로 안전한 예시도 같이 줄게)
                emptyList()
            })

            // ⚠️ 위 repository가 현재 emptyList만 반환하므로, 여기서도 그냥 emptyList로 반환
            // 실제 구현을 아래 "A안 / B안" 중 하나로 바꿔라
            emptyList()
        }

    // ✅ PagingData Flow
    val photos = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PhotoPagingSource(fetcher = fetcher) }
    ).flow.cachedIn(viewModelScope)
}