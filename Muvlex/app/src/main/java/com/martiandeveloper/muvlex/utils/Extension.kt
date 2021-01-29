package com.martiandeveloper.muvlex.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.martiandeveloper.muvlex.R

fun ImageView.load(
    context: Context,
    posterPath: String?,
) {
    Glide.with(context)
        .load(posterPath ?: R.drawable.muvlex_original_logo)
        .placeholder(R.drawable.muvlex_original_logo)
        .error(R.drawable.muvlex_original_logo)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .into(this)
}

fun MaterialButton.enable(context: Context) {
    isEnabled = true
    alpha = 1F
    setTextColor(ContextCompat.getColor(context, R.color.color_supreme_text))
}

fun MaterialButton.disable(context: Context) {
    isEnabled = false
    alpha = .5F
    setTextColor(ContextCompat.getColor(context, R.color.color_regular_text))
}

fun Int.showToast(context: Context) {
    makeText(context, context.getString(this), Toast.LENGTH_SHORT).show()
}

fun View?.navigate(direction: NavDirections) {
    if (this != null) findNavController().navigate(direction)
}

fun AlertDialog.show(view: View) {
    setView(view)
    setCanceledOnTouchOutside(false)
    setCancelable(false)
    show()
}

fun String?.check(): Boolean {
    return this != null && this != "null" && this != ""
}
