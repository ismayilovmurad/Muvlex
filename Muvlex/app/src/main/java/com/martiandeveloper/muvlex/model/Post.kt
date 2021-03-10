package com.martiandeveloper.muvlex.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Post(
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val posterPath: String? = null,
    val rating: String? = null,
    val releaseDate: String? = null,
    val review: String? = null,
    val time: Timestamp? = null,
    val title: String? = null,
    val type: String? = null,
    val uid: String? = null,
    val user: DocumentReference? = null,
    val voteAverage: String? = null
)
