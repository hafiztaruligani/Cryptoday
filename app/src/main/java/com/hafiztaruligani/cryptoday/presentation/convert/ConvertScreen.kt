package com.hafiztaruligani.cryptoday.presentation.convert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.presentation.common.RoundedTextField
import com.hafiztaruligani.cryptoday.presentation.common.Typography
import com.hafiztaruligani.cryptoday.util.CustomColor


@Composable
fun ConvertScreen(
    viewModel: ConvertViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    var amount by remember { mutableStateOf(TextFieldValue()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.BACKGROUND)
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.convert),
            style = typography.titleMedium
        )
        RoundedTextField(
            textValue = amount,
            modifier = Modifier.fillMaxWidth()
        ){
            amount = it
            viewModel.submitAmount(it.text)
        }
        AutoComplete(list = state.value.coins1SearchResult)
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_swap_vertical_circle_24),
            contentDescription = "swap",
            modifier = Modifier
                .align(Alignment.End)
                .size(40.dp)
                .clickable { viewModel.swap() },
            tint = Color.White
        )
        AutoComplete(list = state.value.coins2SearchResult)
        RoundedTextField(
            textValue = TextFieldValue(state.value.result ?: ""),
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        ){}
    }
}

@Preview
@Composable
private fun Test(){
    MaterialTheme(
        typography = Typography
    ){
        ConvertScreen()
    }
}

@Composable
fun AutoComplete(
    list: List<CoinSimple>?
){
    var query by remember { mutableStateOf(TextFieldValue()) }
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        RoundedTextField(
            textValue = query,
            onChange ={
                query = it
                      },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    expanded = it.hasFocus
                }
        )

        AnimatedVisibility(
            visible = expanded
            ) {
            Popup{
                LazyColumn(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .background(CustomColor.LINE_COLOR)
                        .fillMaxHeight(0.5f)
                ){
                    list?.forEach {
                        item(
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Item(coin = it, modifier = Modifier.clickable {
                                expanded = false
                            })
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }//?: run { expanded=false }
                }
            }
        }
    }

}


@Composable
fun Item(coin: CoinSimple, modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
    ) {
        Text(
            text = coin.name,
            style = typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_right_24), // TODO: image
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White)
            .align(Alignment.BottomCenter)
        )
    }
}




























