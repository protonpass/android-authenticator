package proton.android.authenticator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.ui.home.ui.HomeScreen
import proton.android.authenticator.ui.theme.ProtonAuthenticatorTheme

@Composable
internal fun AuthenticatorApp(modifier: Modifier = Modifier) {
    ProtonAuthenticatorTheme {
        Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
            HomeScreen(Modifier.padding(innerPadding))
        }
    }
}
