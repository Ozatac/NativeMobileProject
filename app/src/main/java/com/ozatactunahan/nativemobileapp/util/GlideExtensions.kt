package com.ozatactunahan.nativemobileapp.util

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ozatactunahan.nativemobileapp.R

fun ImageView.loadProductImage(
    imageUrl: String?,
    onLoadFailed: ((String) -> Unit)? = null,
    onLoadSuccess: (() -> Unit)? = null
) {
    if (imageUrl.isNullOrBlank()) {
        setImageResource(R.drawable.placeholder_image)
        return
    }

    try {
        Glide.with(context).load(imageUrl).centerCrop().placeholder(R.drawable.placeholder_image).error(R.drawable.error_image)
            .fallback(R.drawable.placeholder_image).timeout(15000).listener(object : RequestListener<android.graphics.drawable.Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<android.graphics.drawable.Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.w(
                        "Glide",
                        "Failed to load image: $imageUrl"
                    )
                    onLoadFailed?.invoke(imageUrl)
                    return false
                }

                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable,
                    model: Any,
                    target: Target<android.graphics.drawable.Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoadSuccess?.invoke()
                    return false
                }
            }).into(this)
    } catch (e: Exception) {
        setImageResource(R.drawable.placeholder_image)
        Log.e(
            "Glide",
            "Exception loading image: $imageUrl",
            e
        )
        onLoadFailed?.invoke(imageUrl)
    }
}