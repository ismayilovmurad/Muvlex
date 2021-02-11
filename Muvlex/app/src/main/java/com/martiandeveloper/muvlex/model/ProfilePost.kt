package com.martiandeveloper.muvlex.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ProfilePost(
    var time: Timestamp? = null,
    var review: String? = null,
    var item_id: String? = null,
    var title: String? = null,
    var posterPath: String? = null,
    var star: String? = null
)
