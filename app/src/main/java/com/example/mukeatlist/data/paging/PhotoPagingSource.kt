package com.example.mukeatlist.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mukeatlist.data.model.FeedPhoto

class PhotoPagingSource(
    private val fetcher: suspend (page: Int, size: Int) -> List<FeedPhoto>
) : PagingSource<Int, FeedPhoto>() {

    override fun getRefreshKey(state: PagingState<Int, FeedPhoto>): Int? {
        val anchor = state.anchorPosition ?: return null
        val closest = state.closestPageToPosition(anchor)
        return closest?.prevKey?.plus(1) ?: closest?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedPhoto> {
        val page = params.key ?: 1
        val size = params.loadSize.coerceAtMost(30)

        return try {
            val items = fetcher(page, size)
            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (items.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
