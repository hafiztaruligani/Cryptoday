package com.hafiztaruligani.cryptoday.presentation.info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.presentation.common.Typography
import com.hafiztaruligani.cryptoday.presentation.main.MainActivity
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment().also {

            Log.d(TAG, "onCreateView: fragment info")
        }
    }

    private val viewModel by viewModels<InfoViewModel>()
    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            composeView = this
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme(
                typography = Typography
            ) {
                GetInfoScreen()
            }
        }
    }

    @Preview
    @Composable
    private fun GetInfoScreen() {
        InfoScreen(viewModel = viewModel, ::onClickButtonLogin)
    }

    private fun onClickButtonLogin(action: String){
        if(action == requireContext().resources.getString(R.string.login))
            (activity as MainActivity).login()
        else (activity as MainActivity).logout()
    }

}
