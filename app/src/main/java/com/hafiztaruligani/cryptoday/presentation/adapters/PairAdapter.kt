package com.hafiztaruligani.cryptoday.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.hafiztaruligani.cryptoday.databinding.ItemPairBinding

class PairAdapter(val context: Context) : BaseAdapter() {

    var pairs = mutableListOf<String>()

    override fun getCount(): Int {
        return pairs.size
    }

    override fun getItem(p0: Int): Any {
        return pairs[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ItemPairBinding.inflate(LayoutInflater.from(context), p2, false)
        binding.apply {
            this.pair.text = pairs[p0].uppercase()
        }
        return binding.root
    }
}
