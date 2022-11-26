package com.hafiztaruligani.cryptoday.presentation.detail

import android.R
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hafiztaruligani.cryptoday.databinding.FragmentDetailBinding
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.presentation.LoadingBar
import com.hafiztaruligani.cryptoday.presentation.main.MainActivity
import com.hafiztaruligani.cryptoday.util.glide
import com.hafiztaruligani.cryptoday.util.removeLinksUnderline
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment() : Fragment() {

    val viewModel by viewModels<DetailViewModel>()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var loading: LoadingBar

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater)
        animate()

        viewModel.initData(args.coin.id)
        loading = LoadingBar(binding.root.context)
        try {
            bindUI()
        } catch (e: Exception) {
            binding.slider.isVisible = false
        }
        return binding.root
    }

    private fun setLoadingBackground() {
        context?.let {
            val background = ContextCompat.getDrawable(it, R.color.transparent)
            binding.apply {
                if (marketCap.background != ContextCompat.getDrawable(it, R.color.darker_gray)) {
                    marketCap.background = background
                    volume.background = background
                    circulatingSupply.background = background
                    maximumSupply.background = background
                    genesisDate.background = background
                    link.background = background
                    description.background = background
                }
            }
        }
    }

    private fun animate() {
        ViewCompat.setTransitionName(binding.coinItemLayout, args.coin.id)

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        val marketData = args.coin.marketData
        binding.apply {
            Glide.with(root.context)
                .load(args.coin.image)
                .override(logo.width, logo.height)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(logo)
            name.text = args.coin.getTitle()
            currentPrice.text = marketData.fiatFormat(marketData.currentPrice)
            priceChangePercentage.text = marketData.percentageFormatter(
                marketData.priceChangePercentage
            )
        }
    }

    private fun bindUI() {
        setupSlider()

        viewModel.detailUiState.observe(viewLifecycleOwner) { data ->
            loading.state(data.loading)
            if (data.error.isNotBlank()) {
                Toast.makeText(context, data.error, Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
            data.data?.let {
                setLoadingBackground()
                val coin = it.coin
                val marketData = it.coin.marketData
                val coinDetail = it.detail

                binding.apply {

                    currentPrice.text = marketData.fiatFormat(marketData.currentPrice)

                    marketData.priceChangePercentage.let { p ->
                        if (p >= 0) {
                            iconArrow.glide(root.context, com.hafiztaruligani.cryptoday.R.drawable.ic_baseline_arrow_drop_up_24)
                            priceChangePercentage.setTextColor(ContextCompat.getColor(root.context, com.hafiztaruligani.cryptoday.R.color.up))
                        } else {
                            iconArrow.glide(root.context, com.hafiztaruligani.cryptoday.R.drawable.ic_baseline_arrow_drop_down_24)
                            priceChangePercentage.setTextColor(ContextCompat.getColor(root.context, com.hafiztaruligani.cryptoday.R.color.down))
                        }
                    }
                    priceChangePercentage.text = marketData.percentageFormatter(
                        marketData.priceChangePercentage
                    )

                    low.text = marketData.fiatFormat(marketData.low)
                    high.text = marketData.fiatFormat(marketData.high)

                    getSliderValue(marketData)

                    marketCap.text = marketData.fiatFormat(marketData.marketCap)
                    volume.text = marketData.fiatFormat(marketData.volume)
                    circulatingSupply.text =
                        marketData.cryptoFormat(marketData.circulatingSupply, it.coin.symbol)
                    maximumSupply.text =
                        marketData.cryptoFormat(marketData.maxSupply, it.coin.symbol)

                    coinDetail?.let {
                        genesisDate.text = ": ".plus(coinDetail.genesisDate)
                        link.text = ": ".plus(coinDetail.link)

                        val desc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(coinDetail.description.replace("\n", "<br>"), Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            Html.fromHtml(coinDetail.description.replace("\n", "<br>"))
                        }
                        description.text = desc
                        description.removeLinksUnderline(root.context)
                    }

                    setFavourite(btnFavourite, coin.isFavorite)

                    btnFavourite.setOnClickListener {
                        coin.isFavorite = !coin.isFavorite

                        val isSuccess = viewModel.favouriteEvent(coin)
                        if (isSuccess) setFavourite(btnFavourite, coin.isFavorite)
                        else {
                            (activity as MainActivity).login()
                            coin.isFavorite = !coin.isFavorite
                        }
                    }
                }
            }
        }
    }

    private fun setFavourite(btnFavourite: ImageView, favourite: Boolean) {
        if (favourite) btnFavourite.glide(btnFavourite.context, com.hafiztaruligani.cryptoday.R.drawable.ic_favourite_on)
        else btnFavourite.glide(btnFavourite.context, com.hafiztaruligani.cryptoday.R.drawable.ic_favourite_off)
    }

    private fun setupSlider() {
        val activeColor = ColorStateList(
            arrayOf(intArrayOf(-R.attr.state_enabled), intArrayOf(R.attr.state_enabled)),
            intArrayOf(Color.CYAN, Color.CYAN)
        )
        val inactiveColor = ColorStateList(
            arrayOf(intArrayOf(-R.attr.state_enabled), intArrayOf(R.attr.state_enabled)),
            intArrayOf(Color.LTGRAY, Color.LTGRAY)
        )
        val thumbColor = ColorStateList(
            arrayOf(intArrayOf(-R.attr.state_enabled), intArrayOf(R.attr.state_enabled)),
            intArrayOf(Color.BLACK, Color.BLACK)
        )
        binding.apply {
            slider.trackActiveTintList = activeColor
            slider.trackInactiveTintList = inactiveColor
            slider.thumbTintList = thumbColor
            low.text = args.coin.marketData.fiatFormat(args.coin.marketData.low)
            high.text = args.coin.marketData.fiatFormat(args.coin.marketData.high)
            slider.isEnabled = false
        }
    }

    private fun getSliderValue(marketData: MarketData) {
        var l = marketData.low.toFloat()
        var h = marketData.high.toFloat()
        var c = marketData.currentPrice.toFloat()
        binding.apply {

            if (l> h) l = h.also { h = l }
            if (l> c) l = c
            if (c> h) h = c

            slider.valueFrom = l
            slider.valueTo = h
            slider.setValues(c)
        }
    }
}
