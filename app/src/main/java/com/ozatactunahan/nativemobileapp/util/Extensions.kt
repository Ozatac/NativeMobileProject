package com.ozatactunahan.nativemobileapp.util

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ozatactunahan.nativemobileapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun View.clickWithDebounce(debounceTime: Long = Constants.debounceTime, action: (View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action(v)
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun ImageView.downloadFromUrl(url: String?, context: Context) {
    val options = RequestOptions()
        .error(ContextCompat.getDrawable(context, R.color.purple_200))

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .dontAnimate()
        .fitCenter().into(this)
}

/**
 * Fragment'larda Flow'ları lifecycle-aware şekilde collect etmek için extension
 */
inline fun <T> Fragment.collectLatestLifecycleFlow(
    flow: Flow<T>,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest { action(it) }
        }
    }
}

/**
 * Fragment'larda Flow'ları collect etmek için extension
 */
inline fun <T> Fragment.collectLifecycleFlow(
    flow: Flow<T>,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { action(it) }
        }
    }
}