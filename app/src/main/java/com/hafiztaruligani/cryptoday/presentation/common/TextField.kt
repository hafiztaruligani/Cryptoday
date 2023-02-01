package com.hafiztaruligani.cryptoday.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hafiztaruligani.cryptoday.util.CustomColor

@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    textValue: TextFieldValue,
    readOnly: Boolean=false,
    onChange: ((TextFieldValue) -> Unit)
) {
    val shape = RoundedCornerShape(20.dp)

    BasicTextField(
        value = textValue,
        readOnly = readOnly,
        onValueChange = {
            onChange.invoke(it)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .background(CustomColor.EDIT_TEXT_COLOR, shape = shape)
            .padding(all = 18.dp)
    )

}

@Preview
@Composable
private fun Test(){
    var text by remember {
        mutableStateOf(TextFieldValue())
    }
    RoundedTextField(textValue = text, onChange = { text = it })
}