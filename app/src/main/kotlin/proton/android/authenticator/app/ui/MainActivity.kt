/*
 * Copyright (c) 2025 Proton AG
 * This file is part of Proton AG and Proton Authenticator.
 *
 * Proton Authenticator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Authenticator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Authenticator.  If not, see <https://www.gnu.org/licenses/>.
 */

package proton.android.authenticator.app.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import proton.android.authenticator.R
import proton.android.authenticator.app.presentation.MainViewModel
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.navigation.domain.navigators.NavigationNavigator
import proton.android.authenticator.shared.ui.domain.theme.isDarkTheme
import javax.inject.Inject

@AndroidEntryPoint
internal class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    internal lateinit var navigationNavigator: NavigationNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collectLatest { state ->
                    setContent {
                        when (state.appLockState) {
                            AppLockState.LOCKED -> { finishAffinity() }
                            AppLockState.NOT_STARTED -> {}
                            AppLockState.AUTHENTICATING -> {
                                val context = LocalContext.current
                                LaunchedEffect(state.appLockState) {
                                    viewModel.requestReauthentication(context)
                                }
                                CenteredLauncherIcon()
                            }
                            AppLockState.AUTHENTICATED -> isDarkTheme(state.themeType)
                                .also(::setStatusBarTheme)
                                .also { isDarkTheme -> navigationNavigator.NavGraphs(isDarkTheme) }
                        }
                    }
                }
            }
        }
    }

    private fun setStatusBarTheme(isDarkTheme: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView)
            .also { controller -> controller.isAppearanceLightStatusBars = !isDarkTheme }
    }

}

@Composable
fun CenteredLauncherIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )
    }
}
