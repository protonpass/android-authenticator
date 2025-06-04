package proton.android.authenticator.navigation.navigators

import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import proton.android.authenticator.business.steps.domain.StepDestination
import proton.android.authenticator.features.shared.usecases.steps.ObserveStepUseCase
import proton.android.authenticator.navigation.domain.commands.NavigationCommandHandler
import proton.android.authenticator.navigation.domain.graphs.home.HomeNavigationDestination
import proton.android.authenticator.navigation.domain.graphs.home.homeNavigationGraph
import proton.android.authenticator.navigation.domain.graphs.onboarding.OnboardingNavigationDestination
import proton.android.authenticator.navigation.domain.graphs.onboarding.onboardingNavigationGraph
import proton.android.authenticator.navigation.domain.graphs.settings.settingsNavigationGraph
import proton.android.authenticator.navigation.domain.navigators.NavigationNavigator
import proton.android.authenticator.shared.common.domain.dispatchers.SnackbarDispatcher
import proton.android.authenticator.shared.ui.domain.events.ObserveAsUiEvents
import proton.android.authenticator.shared.ui.domain.theme.Theme
import javax.inject.Inject

internal class AppNavigationNavigator @Inject constructor(
    private val observeStepUseCase: ObserveStepUseCase,
    private val navigationCommandHandler: NavigationCommandHandler,
    private val snackbarDispatcher: SnackbarDispatcher
) : NavigationNavigator {

    @Composable
    override fun NavGraphs(isDarkTheme: Boolean) {
        Theme(isDarkTheme = isDarkTheme) {
            val step by observeStepUseCase().collectAsState(initial = null)
            step?.let { currentStep ->
                val startDestination = remember {
                    when (currentStep.destination) {
                        StepDestination.Home -> HomeNavigationDestination
                        StepDestination.Onboarding -> OnboardingNavigationDestination
                    }
                }
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                val navController = rememberNavController(bottomSheetNavigator)
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                val context = LocalContext.current

                DisposableEffect(key1 = navController) {
                    val observer = NavController.OnDestinationChangedListener { _, _, _ ->
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }

                    navController.addOnDestinationChangedListener(observer)

                    onDispose {
                        navController.removeOnDestinationChangedListener(observer)
                    }
                }

                ObserveAsUiEvents(flow = snackbarDispatcher.observe()) { snackbarEvent ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()

                        snackbarHostState.showSnackbar(
                            message = context.getString(snackbarEvent.messageResId),
                            actionLabel = snackbarEvent.action?.let { action ->
                                context.getString(action.nameResId)
                            },
                            duration = SnackbarDuration.Short
                        ).also { snackbarResult ->
                            when (snackbarResult) {
                                SnackbarResult.ActionPerformed -> snackbarEvent.action?.onAction?.invoke()
                                SnackbarResult.Dismissed -> Unit
                            }
                        }
                    }
                }

                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        homeNavigationGraph(snackbarHostState = snackbarHostState) { navCommand ->
                            navigationCommandHandler.handle(navCommand, navController)
                        }

                        onboardingNavigationGraph { navCommand ->
                            navigationCommandHandler.handle(navCommand, navController)
                        }

                        settingsNavigationGraph { navCommand ->
                            navigationCommandHandler.handle(navCommand, navController)
                        }
                    }
                }
            }
        }
    }
}
