package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.model.Series
import com.martiandeveloper.muvlex.service.TmdbApi

class SeriesDataSource(private val query: String, private val api: TmdbApi) :
    PagingSource<Int, Series>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Series> {

        return try {
            val currentLoadingPageKey = params.key ?: 1
            val responseData = mutableListOf<Series>()
            responseData.addAll(
                api.getSeries(query, currentLoadingPageKey).body()?.results ?: emptyList()
            )

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1,
                nextKey = currentLoadingPageKey.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Series>): Int? {
        return state.anchorPosition
    }

}
