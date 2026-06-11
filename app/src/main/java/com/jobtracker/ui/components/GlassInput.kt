package com.jobtracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jobtracker.ui.theme.GlassBorder
import com.jobtracker.ui.theme.GlassWhite
import com.jobtracker.ui.theme.GlassWhiteLight

/**
 * A glass-styled text input field matching the iOS 26 Liquid Glass design.
 */
@Composable
fun GlassInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 5,
    minLines: Int = 1,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    error: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            placeholder = if (placeholder.isNotEmpty()) {
                {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = GlassWhite.copy(alpha = 0.4f)
                    )
                }
            } else null,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = GlassWhite.copy(alpha = 0.7f)
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = error != null,
            supportingText = if (error != null) {
                {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() }
            ),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = GlassWhite,
                unfocusedTextColor = GlassWhite.copy(alpha = 0.8f),
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = GlassWhiteLight.copy(alpha = 0.08f),
                unfocusedContainerColor = GlassWhiteLight.copy(alpha = 0.04f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = GlassWhite.copy(alpha = 0.5f),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = GlassBorder,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorContainerColor = GlassWhiteLight.copy(alpha = 0.04f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error,
                disabledTextColor = GlassWhite.copy(alpha = 0.3f),
                disabledContainerColor = GlassWhiteLight.copy(alpha = 0.02f),
                disabledLabelColor = GlassWhite.copy(alpha = 0.3f),
                disabledIndicatorColor = GlassBorder.copy(alpha = 0.3f)
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
