package com.martiandeveloper.muvlex.service

import com.martiandeveloper.muvlex.model.MovieResult
import com.martiandeveloper.muvlex.model.SeriesResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("search/movie")
    suspend fun getMovie(
        @Query("query") movie: String,
        @Query("page") page: Int
    ): Response<MovieResult>

    @GET("search/tv")
    suspend fun getSeries(
        @Query("query") series: String,
        @Query("page") page: Int
    ): Response<SeriesResult>
}
