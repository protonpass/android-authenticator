package proton.android.authenticator.shared.ui.domain.components.textfields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme

@Composable
fun StandaloneSecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true,
    isError: Boolean = false,
    errorText: UiText? = null,
    isVisible: Boolean = false,
    onVisibilityChange: (Boolean) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Password
    )
) {
    val trailingUiIcon = remember(key1 = isVisible) {
        if (isVisible) {
            R.drawable.ic_eye_slash
        } else {
            R.drawable.ic_eye
        }.let(UiIcon::Resource)
    }

    val visualTransformation = remember(key1 = isVisible) {
        if (isVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        textStyle = Theme.typography.monoNorm2,
        visualTransformation = visualTransformation,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null,
                tint = Theme.colorScheme.textWeak
            )
        },
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                Icon(
                    painter = trailingUiIcon.asPainter(),
                    contentDescription = null,
                    tint = Theme.colorScheme.textWeak
                )
            }
        },
        supportingText = {
            errorText?.let { text ->
                Text(
                    text = text.asString(),
                    style = Theme.typography.captionRegular
                )
            }
        },
        keyboardOptions = keyboardOptions,
        singleLine = isSingleLine,
        isError = isError,
        colors = TextFieldDefaults.colors()
            .copy(
                focusedTextColor = Theme.colorScheme.textNorm,
                unfocusedTextColor = Theme.colorScheme.textNorm,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Theme.colorScheme.inputBorder,
                unfocusedIndicatorColor = Theme.colorScheme.inputBorder,
                disabledIndicatorColor = Theme.colorScheme.inputBorder,
                cursorColor = Theme.colorScheme.accent,
                errorContainerColor = Color.Transparent,
                errorCursorColor = Theme.colorScheme.accent,
                errorIndicatorColor = Theme.colorScheme.signalError,
                errorTextColor = Theme.colorScheme.signalError,
                errorSupportingTextColor = Theme.colorScheme.signalError
            )
    )
}
