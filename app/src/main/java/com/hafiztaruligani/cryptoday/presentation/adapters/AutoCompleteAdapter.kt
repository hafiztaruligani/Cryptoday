package com.hafiztaruligani.cryptoday.presentation.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.hafiztaruligani.cryptoday.databinding.ItemAutocompleteCoinBinding
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.glide

class AutoCompleteAdapter(
    context: Context,
    itemAutocompleteCoinSimple: Int
) : ArrayAdapter<CoinSimple>(context, itemAutocompleteCoinSimple) {

    var coins= mutableListOf<CoinSimple>()

    fun setData(value: List<CoinSimple>) {
        if(value != coins.toList()) {
            coins = value.toMutableList()
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return coins.size
    }

    override fun getItem(position: Int): CoinSimple? {
        return coins[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding : ItemAutocompleteCoinBinding
        var view = convertView
        if(view==null){
            binding = ItemAutocompleteCoinBinding.inflate(LayoutInflater.from(context))
            view = binding.root
        }else{
            binding = ItemAutocompleteCoinBinding.bind(view)
        }
        binding.apply {
            name.text = coins[position].name
            logo.glide(context, coins[position].logo)
        }
        return view
    }

    override fun getFilter(): Filter {

    return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResult = FilterResults()
                filterResult.values = listOf<CoinSimple>()//coins
                filterResult.count = 0//coins.size
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                if (p1!=null && p1.count>0) notifyDataSetChanged()
                else notifyDataSetInvalidated()
            }

        }
        /*return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val result = coins.filter {
                    p0?.let { it1 -> it.name.contains(it1) }?:false
                }

                val filterResult = FilterResults()
                filterResult.values = result
                filterResult.count = result.size
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                Log.d(TAG, "performFiltering: ${p1?.values}")
                if (p1 != null && (p1.count > 0)) {
                    Log.d(TAG, "publishResults: top")
                    coins.clear()
                    (p1.values as List<*>).forEach {
                        if(it is CoinSimple){
                            coins.add(it)
                        }
                    }

                    notifyDataSetChanged();
                } else {
                    Log.d(TAG, "publishResults: bot")
                    coins = defaultCoinSimples.toMutableList()
                    notifyDataSetInvalidated();
                }
                Log.d(TAG, "publishResults1: $defaultCoinSimples")
                Log.d(TAG, "publishResults2: $coins")
            }

        }*/
    }
}