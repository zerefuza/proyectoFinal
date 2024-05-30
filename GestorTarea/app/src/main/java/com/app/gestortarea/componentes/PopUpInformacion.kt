package com.app.gestortarea.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PopUpInformacion(
    titulo: String,
    descripcion: String,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            title = {
                Text(text = titulo)
            },
            text = {
                Text(descripcion)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSuccess()
                    }) {
                    Text("OK")
                }
            }
        )
    }
}
