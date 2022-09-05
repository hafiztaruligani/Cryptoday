package com.hafiztaruligani.cryptoday.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.ItemLoadStateBinding
import com.hafiztaruligani.cryptoday.presentation.LoadingBar
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.delay
import retrofit2.HttpException

class PagingLoadStateAdapter(
    private val retry:()->Unit
): LoadStateAdapter<PagingLoadStateAdapter.ViewHolder>() {

    private lateinit var loading : LoadingBar

    init {
        Log.d(TAG, "pe: created 1")
    }
    inner class ViewHolder (val binding: ItemLoadStateBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            loading = LoadingBar(binding.root.context)
            binding.btn.setOnClickListener {
                retry
            }
            Log.d(TAG, "pe: created 2")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        Log.d(TAG, "pe: created 3")
        return ViewHolder(ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        Log.d(TAG, "pe top: $loadState")
        val isLoading = loadState is LoadState.Loading
        loading.state(isLoading)
        holder.binding.apply {
            message.isVisible = isLoading
            btn.isVisible = isLoading

            if(loadState is LoadState.Error)
               setErrorMessage(loadState.error, this)
        }
    }

    private fun setErrorMessage(loadState: Throwable, binding: ItemLoadStateBinding){
        Log.d(TAG, "pe bot: $loadState")
        binding.apply {
            when {
                loadState is HttpException -> {
                    if (loadState.code() == 426)
                        root.findViewTreeLifecycleOwner()?.lifecycleScope?.launchWhenResumed {
                            btn.isEnabled = false
                            repeat(60) { i ->
                                val m =  root.context.getString(R.string.wait_server_limited).plus("${60 - i}s")
                                binding.message.text = m
                                delay(1000)
                            }
                            binding.btn.isEnabled = true
                        }
                }
                else -> {
                    message.text = root.context.getString(R.string.network_unavailable)
                }
            }
        }
    }



}
