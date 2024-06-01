package com.app.gestortarea.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Alignment

/**
 * Componente composable que muestra un campo de entrada de contraseña.
 *
 * @param titulo                   El título del campo de entrada.
 * @param password                 El valor actual de la contraseña.
 * @param onPasswordChange         La acción a realizar cuando cambia la contraseña.
 * @param passwordVisible          Indica si la contraseña debe mostrarse o no.
 * @param onPasswordVisibilityToggle La acción para alternar la visibilidad de la contraseña.
 */
@Composable
fun PasswordInput(
    titulo:String,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit
) {
    Column {
        Text(
            text = titulo,
            modifier = Modifier.padding(bottom = 10.dp, start = 25.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            singleLine = true,
            placeholder = { Text(text = "Password") },
            modifier = Modifier
                .padding(bottom = 10.dp, start = 25.dp, end = 25.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(imageVector = image, description)
                }
            }
        )
    }
}
