package com.app.gestortarea.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//componentes del boton de envio
@Composable
fun botonEnvio(
    titulo:String,
    tipo:String,
    inputValido: Boolean,
    onClic: () -> Unit
) {
    val cardColor = when (tipo) {
        "normal" -> Color(0xFF0398FB)
        "borrado" -> Color.Red
        "hecho" -> Color.Green
        else -> Color(0xFF0398FB)
    }

    Button(
        onClick = onClic,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 25.dp, end = 25.dp, bottom = 20.dp),
        shape = RoundedCornerShape(4.dp),
        enabled = inputValido,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = cardColor
        )
    ) {
        Text(text = titulo, modifier = Modifier.padding(5.dp))
    }
}
