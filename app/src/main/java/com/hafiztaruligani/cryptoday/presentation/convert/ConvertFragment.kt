package com.hafiztaruligani.cryptoday.presentation.convert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.FragmentConvertBinding
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.presentation.adapters.AutoCompleteAdapter
import com.hafiztaruligani.cryptoday.presentation.common.Typography
import com.hafiztaruligani.cryptoday.util.copyToClipboard
import com.hafiztaruligani.cryptoday.util.glide
import com.hafiztaruligani.cryptoday.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ConvertFragment : Fragment() {

    companion object {
        fun newInstance() = ConvertFragment()
    }

    private val viewModel by viewModels<ConvertViewModel>()
    private lateinit var binding: FragmentConvertBinding

    private lateinit var adapter1: AutoCompleteAdapter
    private lateinit var adapter2: AutoCompleteAdapter
    lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConvertBinding.inflate(layoutInflater).apply {
            viewModel = this@ConvertFragment.viewModel
            lifecycleOwner = this@ConvertFragment.viewLifecycleOwner
        }
        setupAutoComplete()
        setupText()
        return ComposeView(requireContext()).apply{
            composeView = this
        }//binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme(
                typography = Typography
            ) {
                ConvertScreen()
            }
        }
    //bindData()
    }

    private fun setupText() {
        binding.coin2Amount.setOnClickListener {
            it.context.copyToClipboard(binding.coin2Amount.text.toString(), "Convert")
            toast("Copied to the clipboard")
        }
    }

    private fun bindData() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.uiState.collectLatest { uiState ->
                setErrorHandler(uiState)
                setImage(uiState)
                setLoading(uiState)
                setAdapter(uiState)
                setResult(uiState)
            }
        }
    }

    private fun setResult(state: ConvertUiState) = state.apply {
        result?.let { result ->
            binding.coin2Amount.text = result
        }
    }

    private fun setAdapter(state: ConvertUiState) = state.apply {
        coins1SearchResult?.let { adapter1.setData(it) } ?: adapter1.notifyDataSetInvalidated()
        coins2SearchResult?.let { adapter2.setData(it) } ?: adapter2.notifyDataSetInvalidated()
    }

    private fun setLoading(state: ConvertUiState) = state.apply {
        binding.loading1.isVisible = loading1
        binding.loading2.isVisible = loading2

        (!loading1).let {
            binding.logo1.isVisible = it
            binding.coin1Amount.isEnabled = it
            coin1?.name?.let { t ->
                binding.coin1AutoComplete.setAdapter(null)
                binding.coin1AutoComplete.setAdapter(adapter1)
            }
        }
        (!loading2).let {
            binding.logo2.isVisible = it
            binding.coin2Amount.isEnabled = it
            coin2?.name?.let { t ->
                binding.coin2AutoComplete.setAdapter(null)
                binding.coin2AutoComplete.setAdapter(adapter2)
            }
        }
    }

    private fun setImage(state: ConvertUiState) = state.apply {
        coin1?.image?.let { binding.logo1.glide(requireContext(), it) }
        coin2?.image?.let { binding.logo2.glide(requireContext(), it) }
    }

    private fun setErrorHandler(state: ConvertUiState) = state.apply {
        if (error.isNotBlank()) toast(error)
    }

    private fun setupAutoComplete() = binding.apply {

        adapter1 = AutoCompleteAdapter(requireContext(), R.layout.item_autocomplete_coin)
        coin1AutoComplete.setAdapter(adapter1)
        coin1AutoComplete.threshold = 1

        adapter2 = AutoCompleteAdapter(requireContext(), R.layout.item_autocomplete_coin)
        coin2AutoComplete.setAdapter(adapter2)
        coin2AutoComplete.threshold = 1

        coin1AutoComplete.setOnItemClickListener { adapterView, _, i, _ ->
            val coin = adapterView.getItemAtPosition(i) as CoinSimple
            this@ConvertFragment.viewModel.postCoinId1(coin.id)
            coin1AutoComplete.setText(coin.getTitle())
        }
        coin2AutoComplete.setOnItemClickListener { adapterView, _, i, _ ->
            val coin = adapterView.getItemAtPosition(i) as CoinSimple
            this@ConvertFragment.viewModel.postCoinId2(coin.getTitle())
            coin2AutoComplete.setText(coin.name)
        }
    }
}
