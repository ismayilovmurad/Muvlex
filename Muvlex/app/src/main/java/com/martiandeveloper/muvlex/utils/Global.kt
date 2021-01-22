package com.martiandeveloper.muvlex.utils

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.martiandeveloper.muvlex.R

var isNetworkAvailable = false
var appLanguage = "en"
lateinit var searchResult: MutableLiveData<String>

fun loadImage(
    context: Context,
    posterPath: String?,
    imageView: ImageView
) {
    Glide.with(context)
        .load(posterPath ?: R.drawable.muvlex_original_logo)
        .placeholder(R.drawable.muvlex_original_logo)
        .error(R.drawable.muvlex_original_logo)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .into(imageView)
}
