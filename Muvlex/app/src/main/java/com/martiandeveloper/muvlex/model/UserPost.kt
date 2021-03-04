package com.martiandeveloper.muvlex.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserPost(
    var time: Timestamp? = null,
    var review: String? = null,
    var id: String? = null,
    var title: String? = null,
    var posterPath: String? = null,
    var rating: String? = null
)
