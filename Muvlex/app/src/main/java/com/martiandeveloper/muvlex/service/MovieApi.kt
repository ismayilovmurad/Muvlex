package com.martiandeveloper.muvlex.service

import com.martiandeveloper.muvlex.model.MovieResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("search/movie")
    suspend fun getMovie(
        @Query("query") movie: String,
        @Query("page") page: Int
    ): Response<MovieResult>
}
