package com.hafiztaruligani.cryptoday.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.ItemLoadStateBinding
import com.hafiztaruligani.cryptoday.util.Cons.BANNED_TIME
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.delay
import retrofit2.HttpException

class PagingLoadStateAdapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val retry: ()->Unit
): LoadStateAdapter<PagingLoadStateAdapter.ViewHolder>() {

    companion object {
        const val NETWORK_SERVER_LIMITED = 429
    }
    inner class ViewHolder (val binding: ItemLoadStateBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btn.setOnClickListener {
                retry.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        val isLoading = loadState is LoadState.Loading
        holder.binding.apply {
            loading.isVisible = isLoading
            message.isVisible = !isLoading
            btn.isVisible = !isLoading

            if(loadState is LoadState.Error)
                setErrorMessage(loadState.error, this)
        }
    }

    private fun setErrorMessage(loadState: Throwable, binding: ItemLoadStateBinding){
        Log.d(TAG, "pe bot: $loadState")
        binding.apply {
            if (loadState is HttpException) {
                when (loadState.code()){
                    NETWORK_SERVER_LIMITED -> {
                        Log.d(TAG, "setErrorMessage: pe server limited top")
                        lifecycleCoroutineScope.launchWhenResumed {
                            Log.d(TAG, "setErrorMessage: pe server limited bot")
                            btn.isVisible = false
                            repeat(BANNED_TIME) { i ->
                                val m = root.context.getString(R.string.wait_server_limited)
                                    .plus(" ${BANNED_TIME - i}s")
                                message.text = m
                                delay(1000)
                            }
                            retry.invoke()
                        }
                    }
                    else -> {
                        message.text = root.context.getString(R.string.network_unavailable).plus(" (${loadState.code()})")
                    }
                }
            }
            else {
                message.text = root.context.getString(R.string.network_unavailable)
            }
        }
    }
}
