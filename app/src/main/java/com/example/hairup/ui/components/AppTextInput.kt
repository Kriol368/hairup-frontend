package com.example.hairup.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFFD4AF37),
            unfocusedTextColor = Color(0xFFD4AF37),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFFD4AF37),
            unfocusedIndicatorColor = Color(0xFF8B5E3C),
            focusedLabelColor = Color(0xFFD4AF37),
            unfocusedLabelColor = Color(0xFFB0B0B0),
            cursorColor = Color(0xFFD4AF37)
        )
    )
}