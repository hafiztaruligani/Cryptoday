package com.hafiztaruligani.cryptoday.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.util.Cons.TAG

class LoadingBar(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.loading_bar)
        setCancelable(false)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun state(value : Boolean){
        Log.d(TAG, "state: called $value")
        if (value) this@LoadingBar.show()
        else this@LoadingBar.dismiss()
    }

    override fun onBackPressed() {
    }
}