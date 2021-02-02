package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.service.TmdbApi

class MovieDataSource(private val movie: String, private val apiService: TmdbApi) :
    PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {

        return try {
            val currentLoadingPageKey = params.key ?: 1
            val responseData = mutableListOf<Movie>()
            responseData.addAll(
                apiService.getMovie(movie, currentLoadingPageKey).body()?.results ?: emptyList()
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

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        TODO("Not yet implemented")
    }

}
