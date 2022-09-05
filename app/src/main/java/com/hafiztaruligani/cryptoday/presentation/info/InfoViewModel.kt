package com.hafiztaruligani.cryptoday.presentation.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class InfoViewModel : ViewModel() {
    val _a= MutableStateFlow(listOf(0)).apply {

    }
    val a : StateFlow<List<Int>> = _a

    init {
        viewModelScope.launch {
            var i = 1
            while (true) {
               val aa = _a.value.toMutableList()
                aa.add(i)
                _a.emit(aa.toList())
                //Log.d(TAG, "collecting viewmodel: $i || ${a.value}")
                i++
                delay(2000)
            }
        }

    }
}