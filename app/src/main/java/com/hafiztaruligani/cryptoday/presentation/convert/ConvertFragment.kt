package com.hafiztaruligani.cryptoday.presentation.convert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.FragmentConvertBinding
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.presentation.adapters.AutoCompleteAdapter
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ConvertFragment : Fragment() {

    companion object {
        fun newInstance() = ConvertFragment().also {
            Log.d(TAG, "onCreateView: fragment convert")
        }
    }

    private val viewModel by viewModels<ConvertViewModel>()
    private lateinit var binding: FragmentConvertBinding

    private var coin1Focus = false
    private var coin2Focus = false

    private lateinit var adapter1: AutoCompleteAdapter
    private lateinit var adapter2: AutoCompleteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConvertBinding.inflate(layoutInflater).apply {
            viewmodel = viewModel
        }
        setupAutoComplete()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.uiState.collectLatest { uiState ->
                uiState.apply {

                    Log.d(TAG, "uistate: $loading1 ${coins1SearchResult?.size ?: "null"}")

                    if ((!loading1 && coins1SearchResult?.size == 0) || (!loading2 && coins2SearchResult?.size == 0)) {
                        Toast.makeText(requireContext(), "Coin Not Found", Toast.LENGTH_LONG).show()
                    }

                    if (error.isNotBlank())
                        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()

                    coin1?.image?.let { binding.logo1.glide(requireContext(), it) }
                    coin2?.image?.let { binding.logo2.glide(requireContext(), it) }

                    binding.loading1.isVisible = loading1
                    binding.loading2.isVisible = loading2

                    (!loading1).let {
                        binding.logo1.isVisible = it
                        binding.coin1Amount.isEnabled = it
                        coin1?.name?.let { t ->
                            binding.coin1AutoComplete.setAdapter(null)
                            binding.coin1AutoComplete.setText(t)
                            binding.coin1AutoComplete.setAdapter(adapter1)
                        }
                    }
                    (!loading2).let {
                        binding.logo2.isVisible = it
                        binding.coin2Amount.isEnabled = it
                        coin2?.name?.let { t ->
                            binding.coin2AutoComplete.setAdapter(null)
                            binding.coin2AutoComplete.setText(t)
                            binding.coin2AutoComplete.setAdapter(adapter2)
                        }
                    }

                    coins1SearchResult?.let { adapter1.setData(it) } ?: adapter1.notifyDataSetInvalidated()
                    coins2SearchResult?.let { adapter2.setData(it) } ?: adapter2.notifyDataSetInvalidated()

                    result?.let { result ->
                        if (!coin1Focus) binding.coin1Amount.setText(result.first)
                        if (!coin2Focus) binding.coin2Amount.setText(result.second)
                    }
                }
            }
        }
    }

    private fun setupAutoComplete() {
        binding.apply {

            adapter1 = AutoCompleteAdapter(requireContext(), R.layout.item_autocomplete_coin)
            coin1AutoComplete.setAdapter(adapter1)
            coin1AutoComplete.threshold = 1

            adapter2 = AutoCompleteAdapter(requireContext(), R.layout.item_autocomplete_coin)
            coin2AutoComplete.setAdapter(adapter2)
            coin2AutoComplete.threshold = 1

            coin1AutoComplete.setOnItemClickListener { adapterView, _, i, _ ->
                val coin = adapterView.getItemAtPosition(i) as CoinSimple
                viewModel.postCoinId1(coin.id)
                coin1AutoComplete.setText("")
            }
            coin2AutoComplete.setOnItemClickListener { adapterView, _, i, _ ->
                val coin = adapterView.getItemAtPosition(i) as CoinSimple
                viewModel.postCoinId2(coin.id)
                coin2AutoComplete.setText("")
            }

            coin1Amount.addTextChangedListener {
                if (it.toString().isNotBlank() && it.toString().last() != '.')
                    viewModel.postCoinAmount1(it.toString())
            }
            coin2Amount.addTextChangedListener {
                if (it.toString().isNotBlank() && it.toString().last() != '.')
                    viewModel.postCoinAmount2(it.toString())
            }
            coin1Amount.setOnFocusChangeListener { _, b ->
                coin1Focus = b
            }
            coin2Amount.setOnFocusChangeListener { _, b ->
                coin2Focus = b
            }
        }
    }
}
