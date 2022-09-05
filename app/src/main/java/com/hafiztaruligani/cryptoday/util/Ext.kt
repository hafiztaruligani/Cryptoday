package com.hafiztaruligani.cryptoday.util

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hafiztaruligani.cryptoday.R

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
    return toString().drop(1).dropLast(1)
}


//fun Any?.toInt(): Int = this.toString().toInt()
//fun Any?.toDouble(): Double = this.toString().toDouble()