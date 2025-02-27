package proton.android.authenticator.shared.ui.domain.components.images

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.models.UiImage
import proton.android.authenticator.shared.ui.domain.models.UiText

internal class LocalImageComponentDelegate(
    private val modifier: Modifier,
    private val image: UiImage,
    private val contentDescription: UiText?,
    private val alignment: Alignment
) : ImageComponent {

    @Composable
    override fun Render() {
        Image(
            modifier = modifier,
            painter = image.asPainter(),
            contentDescription = contentDescription?.asString(),
            alignment = alignment
        )
    }

}
