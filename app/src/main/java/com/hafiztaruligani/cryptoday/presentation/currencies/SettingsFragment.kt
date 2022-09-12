package com.hafiztaruligani.cryptoday.presentation.currencies

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.FragmentSettingsBinding
import com.hafiztaruligani.cryptoday.domain.usecase.SortBy
import com.hafiztaruligani.cryptoday.presentation.adapters.PairAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class SettingsFragment : DialogFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by activityViewModels<CurrenciesViewModel>()
    private lateinit var spinnerAdapter : PairAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)


        bindSort()
        bindCurrenciesPair()
        return binding.root
    }

    private fun bindCurrenciesPair() {
        context?.let {
            spinnerAdapter = PairAdapter(it)
            binding.spinner.adapter = spinnerAdapter
/*
            lifecycleScope.launchWhenResumed {
                viewModel.currenciesPair.collect(){ list->
                    spinnerAdapter.pairs = list.toMutableList()
                }
            }*/
            /*val popup: Field = AppCompatSpinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val popupWindow = popup[binding.spinner] as androidx.appcompat.widget.ListPopupWindow
            popupWindow.height = 700*/
        }
    }

    private fun bindSort() {
        SortBy().getAllType().forEach {
            val radioButton = RadioButton(context)
            radioButton.id = it.id
            radioButton.text = it.presentationString
            radioButton.setTextColor(Color.WHITE)
            binding.sortBy.addView(radioButton)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {

            combine(viewModel.currenciesPair,viewModel.coinsOrder){ a,b ->
                Pair(a,b)
            }.collectLatest {
                val currenciesPair = it.first
                val coinsOrder = it.second

                spinnerAdapter.pairs = currenciesPair.toMutableList()
                spinnerAdapter.notifyDataSetChanged()

                withContext(Dispatchers.Main){
                    binding.sortBy.check(coinsOrder.sortBy.id)
                    val index = currenciesPair.indexOf(coinsOrder.currencyPair)
                    binding.spinner.setSelection(index)
                }
            }
        }
        binding.btnApply.setOnClickListener { setResult() }

    }

    private fun setResult() {
        viewModel.postSortBy(
            binding.sortBy.checkedRadioButtonId
        )
        viewModel.postUserCurrencyPair(
            binding.spinner.selectedItem.toString()
        )
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

}
