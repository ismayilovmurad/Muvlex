package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import com.martiandeveloper.muvlex.model.Movie
import com.martiandeveloper.muvlex.service.MovieApi

class MovieDataSource(private val movie: String, private val apiService: MovieApi) :
    PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {

        return try {
            val currentLoadingPageKey = params.key ?: 1
            val responseData = mutableListOf<Movie>()
            responseData.addAll(
                apiService.getMovie(movie, currentLoadingPageKey).body()?.results ?: emptyList()
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
