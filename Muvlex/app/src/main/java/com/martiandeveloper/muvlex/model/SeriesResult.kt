package com.martiandeveloper.muvlex.model

import com.squareup.moshi.Json

data class SeriesResult(
    @Json(name = "page")
    val page: Int?,
    @Json(name = "results")
    val results: List<Series>?,
    @Json(name = "total_pages")
    val totalPages: Int?,
    @Json(name = "total_results")
    val totalResults: Int?
)
