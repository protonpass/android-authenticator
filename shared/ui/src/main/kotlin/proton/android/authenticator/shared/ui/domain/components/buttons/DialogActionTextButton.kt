package proton.android.authenticator.shared.ui.domain.components.buttons

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme

@Composable
internal fun DialogActionTextButton(
    text: UiText,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    textColor: Color = Theme.colorScheme.accent
) {
    TextButton(
        modifier = modifier,
        enabled = isEnabled,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors()
            .copy(
                contentColor = textColor,
                disabledContentColor = textColor.copy(alpha = 0.38f)
            )
    ) {
        Text(
            text = text.asString(),
            style = Theme.typography.body2Regular
        )
    }
}
