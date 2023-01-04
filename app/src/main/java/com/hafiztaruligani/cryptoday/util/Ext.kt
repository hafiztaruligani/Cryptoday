package com.hafiztaruligani.cryptoday.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hafiztaruligani.cryptoday.R
import java.io.IOException
import java.math.BigDecimal

fun TextView.removeLinksUnderline(context: Context) {
    val spannable = SpannableString(text)
    for (i in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(
            object : URLSpan(i.url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(context, R.color.description_text)
                }
            },
            spannable.getSpanStart(i), spannable.getSpanEnd(i), 0
        )
    }
    text = spannable
}

fun List<Any>.removeBracket(): String {
    if (isEmpty()) return ""
    return toString().drop(1).dropLast(1)
}
fun String.convertIntoList(): List<String> {
    if (this.contains("["))
        return this.replace(" ", "").replace("[", "").replace("]", "").split(',')
    else throw (IOException("cannot convert non list string format into list"))
}

fun ImageView.glide(context: Context, resource: Any) {
    Glide.with(context).load(resource)
        .override(width, height)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun Fragment.toast(msg: String, isShowLong: Boolean = true) {
    Toast.makeText(
        requireContext(),
        msg,
        if (isShowLong) Toast.LENGTH_LONG
        else Toast.LENGTH_SHORT
    ).show()
}

fun BigDecimal.notZero(): Boolean = this != (0).toBigDecimal()

fun Context.copyToClipboard(text: String, label: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

// fun Any?.toInt(): Int = this.toString().toInt()
// fun Any?.toDouble(): Double = this.toString().toDouble()
