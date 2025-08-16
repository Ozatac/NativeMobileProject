package com.ozatactunahan.nativemobileapp.di

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        
        val memoryCacheSizeBytes = 1024 * 1024 * 25
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
