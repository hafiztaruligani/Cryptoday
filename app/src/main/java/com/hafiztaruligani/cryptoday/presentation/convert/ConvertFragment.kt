package com.hafiztaruligani.cryptoday.presentation.convert

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.FragmentConvertBinding
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.launch

class ConvertFragment : Fragment() {

    companion object {
        fun newInstance() = ConvertFragment().also {
            Log.d(TAG, "onCreateView: fragment convert")
        }
    }

    private val viewModel by viewModels<ConverViewModel>()
    private lateinit var binding: FragmentConvertBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConvertBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}