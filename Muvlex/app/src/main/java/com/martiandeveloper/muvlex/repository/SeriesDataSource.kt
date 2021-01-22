package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import com.martiandeveloper.muvlex.model.Series
import com.martiandeveloper.muvlex.service.TmdbApi

class SeriesDataSource(private val series: String, private val apiService: TmdbApi) :
    PagingSource<Int, Series>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Series> {

        return try {
            val currentLoadingPageKey = params.key ?: 1
            val responseData = mutableListOf<Series>()
            responseData.addAll(
                apiService.getSeries(series, currentLoadingPageKey).body()?.results ?: emptyList()
            )
            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = currentLoadingPageKey.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

}
