package com.app.gestortarea.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputComun(
    placeholder: String,
    value: String,
    titulo:String,
    onvalueChange: (String) -> Unit
) {

    Column {
        Text(text = titulo,
            modifier = Modifier.padding(bottom = 10.dp, start = 25.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onvalueChange,
            placeholder = { Text(text = placeholder) },
            modifier = Modifier
                .padding(bottom = 10.dp, start = 25.dp, end = 25.dp)
                .fillMaxWidth()
        )
    }
}
