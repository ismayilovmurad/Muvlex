package com.martiandeveloper.muvlex.model

import com.google.firebase.firestore.DocumentReference

data class User(
    val bio: String? = null,
    val email: String? = null,
    val followers: List<DocumentReference>? = null,
    val following: List<DocumentReference>? = null,
    val uid: String? = null,
    val username: String? = null
)
