package com.martiandeveloper.muvlex.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var username: String? = null,
    var user_id: String? = null,
)