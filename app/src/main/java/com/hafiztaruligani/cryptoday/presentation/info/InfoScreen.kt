package com.hafiztaruligani.cryptoday.presentation.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.util.CustomColor

@Composable
fun InfoScreen(
    viewModel: InfoViewModel = hiltViewModel(),
    onClick: (action: String) -> Unit
){
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.BACKGROUND)
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TitleAndDesc(state)
        LoginButton(
            userName = state.userName,
            onClick::invoke
        )
    }
}

@Composable
private fun TitleAndDesc(state: InfoUIState) {
    Column {
        Text(text = stringResource(id = R.string.welcome), style = MaterialTheme.typography.titleMedium)
        if (state.userName.isNotEmpty()) {
            Text(text = state.userName, style = MaterialTheme.typography.bodyLarge)
        }
        Text(text = state.description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun LoginButton(userName: String, onClick: (action: String)->Unit){
    val text = if(userName.isEmpty()) stringResource(id = R.string.login)
    else stringResource(id = R.string.logout)
    Button(
        onClick = {onClick.invoke(text)},
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(CustomColor.EDIT_TEXT_COLOR)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            Icon(painterResource(id = R.drawable.ic_baseline_keyboard_arrow_right_24),"")
        }
    }
}
