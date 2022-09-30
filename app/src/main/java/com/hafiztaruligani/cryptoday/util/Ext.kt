package com.hafiztaruligani.cryptoday.util

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hafiztaruligani.cryptoday.R
import java.io.IOException

fun TextView.removeLinksUnderline(context: Context) {
    val spannable = SpannableString(text)
    for (i in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(object : URLSpan(i.url) {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(context,R.color.description_text)
            }
        }, spannable.getSpanStart(i), spannable.getSpanEnd(i), 0)
    }
    text = spannable
}

fun List<Any>.removeBracket(): String{
    if (isEmpty()) return ""
    return toString().drop(1).dropLast(1)
}
fun String.convertIntoList(): List<String>{
    if (this.contains("["))
        return this.replace(" ","").replace("[","").replace("]","").split(',')
    else throw (IOException("cannot convert non list string format into list"))
}

fun ImageView.glide(context: Context, resource: Any){
    Glide.with(context).load(resource)
        .override(width, height)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

//fun Any?.toInt(): Int = this.toString().toInt()
//fun Any?.toDouble(): Double = this.toString().toDouble()