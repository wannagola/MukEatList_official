package com.example.mukeatlist.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mukeatlist.data.model.FeedPhoto
import com.example.mukeatlist.data.paging.PhotoPagingSource
import kotlinx.coroutines.flow.Flow

class PhotoRepository(
    private val fetcher: suspend (page: Int, size: Int) -> List<FeedPhoto>
) {
    fun pagingFlow(): Flow<PagingData<FeedPhoto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 21,
                prefetchDistance = 6,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(fetcher) }
        ).flow
    }
}
