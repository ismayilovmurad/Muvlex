package com.martiandeveloper.muvlex.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val bio: String? = null,
    val email: String? = null,
    val followers: List<String>? = null,
    val following: List<String>? = null,
    val picture: String? = null,
    val uid: String? = null,
    val username: String? = null
) : Parcelable
