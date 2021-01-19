package com.martiandeveloper.muvlex.model

import com.squareup.moshi.Json

data class MovieResult(
    @Json(name = "page")
    val page: Int?,
    @Json(name = "results")
    val results: List<Movie>?,
    @Json(name = "total_pages")
    val totalPages: Int?,
    @Json(name = "total_results")
    val totalResults: Int?
)
