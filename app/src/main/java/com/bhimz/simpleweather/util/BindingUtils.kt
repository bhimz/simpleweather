package com.bhimz.simpleweather.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("imageUrl")
fun ImageView.loadImage(imageUrl: String?) =
    Glide.with(context).load(imageUrl).into(this)

@BindingAdapter(value = ["dateInMillis", "dateFormat"], requireAll = false)
fun TextView.formatDateText(dateInMillis: Long, dateFormat: Any?) {
    text = SimpleDateFormat(
        dateFormat?.toString() ?: "dd-MM-yyyy",
        Locale.US
    ).format(Date(dateInMillis * 1000))
}
