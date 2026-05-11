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

package proton.android.authenticator.features.home.master.presentation

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.business.entries.application.syncall.SyncEntriesReason
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsSortingType
import proton.android.authenticator.features.home.master.R
import proton.android.authenticator.features.home.master.usecases.ObserveEntryCodesUseCase
import proton.android.authenticator.features.shared.R as sharedR
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.features.home.master.usecases.SortEntriesUseCase
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.entries.usecases.SyncEntriesModelsUseCase
import proton.android.authenticator.features.shared.usecases.applock.ObserveAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.backups.ObserveBackupUseCase
import proton.android.authenticator.features.shared.usecases.backups.UpdateBackupUseCase
import proton.android.authenticator.features.shared.usecases.clipboards.CopyToClipboardUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.features.shared.usecases.snackbars.DispatchSnackbarEventUseCase
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds
import proton.android.authenticator.shared.ui.R as uiR

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class HomeMasterViewModel @Inject constructor(
    observeEntryModelsUseCase: ObserveEntryModelsUseCase,
    observeEntryCodesUseCase: ObserveEntryCodesUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val dispatchSnackbarEventUseCase: DispatchSnackbarEventUseCase,
    private val sortEntriesUseCase: SortEntriesUseCase,
    private val syncEntriesModelsUseCase: SyncEntriesModelsUseCase,
    private val timeProvider: TimeProvider,
    private val observeBackupUseCase: ObserveBackupUseCase,
    private val updateBackupUseCase: UpdateBackupUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val observeAppLockStateUseCase: ObserveAppLockStateUseCase,
    private val telemetryManager: AuthenticatorTelemetryManager
) : ViewModel() {

    private val entrySearchQueryState = mutableStateOf(value = SEARCH_QUERY_DEFAULT_VALUE)

    private val entrySearchQueryFlow = snapshotFlow { entrySearchQueryState.value }

    private val eventFlow = MutableStateFlow<HomeMasterEvent>(value = HomeMasterEvent.Idle)

    private val isRefreshingFlow = MutableStateFlow(value = false)

    private val settingsFlow = observeSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = Settings.Default
        )

    private val backupFlow = observeBackupUseCase()

    private val showWarningPasswordDialog = MutableStateFlow(false)
    private val enableWarningMessage = MutableStateFlow(false)

    val syncDialogState = combine(
        showWarningPasswordDialog,
        enableWarningMessage,
        ::SyncDialogState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SyncDialogState()
    )

    private val screenModelFlow = combine(
        eventFlow,
        entrySearchQueryFlow,
        isRefreshingFlow,
        ::HomeMasterScreenModel
    )

    @OptIn(FlowPreview::class)
    private val entrySearchQueryDebouncedFlow = entrySearchQueryFlow
        .debounce { entrySearchQuery ->
            if (entrySearchQuery.isEmpty()) SEARCH_QUERY_EMPTY_DEBOUNCE_MILLIS
            else SEARCH_QUERY_DEBOUNCE_MILLIS
        }

    private val entriesFlow = combine(
        observeEntryModelsUseCase(),
        entrySearchQueryDebouncedFlow
    ) { entryModels, searchQuery ->
        entryModels.filter { entryModel ->
            if (searchQuery.isEmpty()) {
                true
            } else {
                entryModel.issuer.contains(searchQuery, true) ||
                    entryModel.name.contains(searchQuery, true)
            }
        }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        replay = 1
    )

    private val entryCodesRemainingTimeTickerFlow = flow {
        while (coroutineContext.isActive) {
            emit(Unit)

            delay(1.seconds)
        }
    }

    private val entryCodesRemainingTimesFlow = combine(
        entriesFlow,
        entryCodesRemainingTimeTickerFlow
    ) { entries, _ ->
        entries.associate { entry ->
            entry.period to timeProvider.remainingPeriodSeconds(entry.period)
        }
    }

    private val entryModelsMapFlow = entriesFlow
        .flatMapLatest { entries ->
            combine(
                observeEntryCodesUseCase(entries),
                settingsFlow
            ) { entryCodes, settings ->
                val sortedEntries = entries.sort(sortingType = settings.sortingType)
                val uriToCodeMap =
                    entries.zip(entryCodes).associate { (entry, code) -> entry.uri to code }
                sortedEntries.mapNotNull { entry ->
                    uriToCodeMap[entry.uri]?.let { code ->
                        HomeMasterEntryModel(entry, code)
                    }
                }.associateBy { entryModel -> entryModel.id }
            }
        }

    internal val stateFlow: StateFlow<HomeMasterState> = combine(
        screenModelFlow,
        entryModelsMapFlow,
        entryCodesRemainingTimesFlow,
        settingsFlow
    ) { screenModel, entryModelsMap, entryCodesRemainingTimes, settings ->
        when {
            screenModel.searchQuery.isEmpty() && entryModelsMap.isEmpty() -> {
                HomeMasterState.Empty(
                    event = screenModel.event,
                    isRefreshing = screenModel.isRefreshing,
                    settings = settings
                )
            }

            entryModelsMap.isEmpty() -> {
                HomeMasterState.EmptySearch(
                    searchQuery = screenModel.searchQuery,
                    settings = settings
                )
            }

            else -> {
                HomeMasterState.Ready(
                    event = screenModel.event,
                    searchQuery = screenModel.searchQuery,
                    isRefreshing = screenModel.isRefreshing,
                    entryModelsMap = entryModelsMap,
                    entryCodesRemainingTimes = entryCodesRemainingTimes,
                    settings = settings
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeMasterState.Loading
    )

    init {
        viewModelScope.launch {
            settingsFlow
                .flatMapLatest {
                    if (it.appLockType == SettingsAppLockType.Biometric) {
                        observeAppLockStateUseCase()
                    } else {
                        flowOf(AppLockState.AuthNotRequired)
                    }
                }
                .filter { appLockState -> appLockState == AppLockState.AuthNotRequired }
                .take(1)
                .collect { appLockState ->
                    checkDisplayWarning()
                }
        }

        viewModelScope.launch {
            settingsFlow
                .map { it.hasUndecryptableEntries to it.isUndecryptableEntriesWarningDismissed }
                .distinctUntilChanged()
                .filter { (hasUndecryptableEntries, isUndecryptableEntriesWarningDismissed) ->
                    hasUndecryptableEntries && !isUndecryptableEntriesWarningDismissed && !isRefreshingFlow.value
                }
                .collect {
                    dispatchSnackbarEventUseCase(undecryptableEntriesWarningSnackbar())
                }
        }
    }

    private suspend fun checkDisplayWarning() {
        backupFlow.firstOrNull()?.also { backupModel ->
            val shouldDisplayWarningDialog = backupModel.isEnabled &&
                backupModel.encryptedPassword.isNullOrEmpty() &&
                backupModel.directoryUri.toString().isNotEmpty()

            if (shouldDisplayWarningDialog) {
                showWarningPasswordDialog.update { true }
                enableWarningMessage.update { backupModel.count > 0 }
                disableBackup()
            }
        }
    }

    private fun disableBackup() {
        viewModelScope.launch {
            updateBackupUseCase(
                newBackup = Backup(
                    isEnabled = false,
                    directoryUri = Uri.EMPTY,
                    encryptedPassword = null,
                    frequencyType = BackupFrequencyType.Daily,
                    count = 0,
                    lastBackupMillis = 0
                )
            )
        }
    }

    internal fun onConfirmAlertBackupDialog() {
        showWarningPasswordDialog.update { false }
        enableWarningMessage.update { false }
    }

    internal fun onDismissAlertBackupDialog() {
        showWarningPasswordDialog.update { false }
        enableWarningMessage.update { false }
    }

    internal fun onConsumeEvent(event: HomeMasterEvent) {
        eventFlow.compareAndSet(expect = event, update = HomeMasterEvent.Idle)
    }

    internal fun onCopyEntryCode(entryId: String) {
        val readyState = stateFlow.value as? HomeMasterState.Ready ?: return
        val entry = readyState.entryModel(entryId) ?: return
        copyToClipboardUseCase(text = entry.currentCode, isSensitive = readyState.areCodesHidden)
            .let { isSupported ->
                SnackbarEvent(messageResId = R.string.home_snackbar_message_entry_copied)
                    .takeIf { !isSupported }
            }
            ?.also { event ->
                viewModelScope.launch {
                    dispatchSnackbarEventUseCase(event)
                }
            }
        viewModelScope.launch { telemetryManager.sendEvent(AuthenticatorTelemetryEvent.CopyCode) }
    }

    internal fun onEntriesSorted(newSortingMap: Map<String, Int>) {
        val readyState = stateFlow.value as? HomeMasterState.Ready ?: return
        viewModelScope.launch {
            sortEntriesUseCase(
                entryModels = readyState.entryModels.map(HomeMasterEntryModel::entryModel),
                newSortingMap = newSortingMap
            ).fold(
                onFailure = {
                    SnackbarEvent(messageResId = R.string.home_snackbar_message_entry_rearrange_failed)
                        .also { event -> dispatchSnackbarEventUseCase(event) }
                },
                onSuccess = {
                    eventFlow.update { HomeMasterEvent.OnEntriesSorted }
                }
            )
        }
    }

    internal fun onRefreshEntries(isSyncEnabled: Boolean) {
        viewModelScope.launch {
            isRefreshingFlow.update { true }

            // Workaround to avoid a bug on pull to refresh indicator that stays on the screen forever
            // when refreshing state is updated so fast, see the following issue:
            // https://issuetracker.google.com/issues/248274004
            delay(timeMillis = 20)

            if (!isSyncEnabled) {
                SnackbarEvent(
                    messageResId = R.string.home_snackbar_message_entry_sync_disabled,
                    action = SnackbarEvent.Action(
                        nameResId = uiR.string.action_enable,
                        onAction = {
                            eventFlow.update { HomeMasterEvent.OnEnableSync }
                        }
                    )
                )
                    .also { event -> dispatchSnackbarEventUseCase(event) }
                    .also { isRefreshingFlow.update { false } }
                    .also { return@launch }
            }

            syncEntriesModelsUseCase(forceRefresh = true).fold(
                onFailure = { reason ->
                    AuthenticatorLogger.w(tag = TAG, message = "Entries sync failed: $reason")

                    when (reason) {
                        SyncEntriesReason.Unauthorized -> {
                            SnackbarEvent(
                                messageResId = R.string.home_snackbar_message_entry_sync_unauthorized,
                                action = SnackbarEvent.Action(
                                    nameResId = uiR.string.action_login,
                                    onAction = {
                                        eventFlow.update { HomeMasterEvent.OnEnableSync }
                                    }
                                )
                            )
                        }

                        SyncEntriesReason.KeyNotFound,
                        SyncEntriesReason.Unknown,
                        SyncEntriesReason.UserNotFound -> {
                            SnackbarEvent(
                                messageResId = R.string.home_snackbar_message_entry_sync_failed
                            )
                        }
                    }.also { event -> dispatchSnackbarEventUseCase(event) }
                },
                onSuccess = { undecryptableCount ->
                    AuthenticatorLogger.i(tag = TAG, message = "Entries sync succeeded")
                    if (undecryptableCount > 0 && !settingsFlow.value.isUndecryptableEntriesWarningDismissed) {
                        dispatchSnackbarEventUseCase(undecryptableEntriesWarningSnackbar())
                    }
                }
            ).also {
                isRefreshingFlow.update { false }
            }
        }
    }

    internal fun onUpdateEntrySearchQuery(newSearchQuery: String) {
        entrySearchQueryState.value = newSearchQuery.trimStart()
    }

    private fun List<EntryModel>.sort(sortingType: SettingsSortingType) = when (sortingType) {
        SettingsSortingType.CreatedAsc -> sortedBy(EntryModel::createdAt)
        SettingsSortingType.CreatedDesc -> sortedByDescending(EntryModel::createdAt)
        SettingsSortingType.Manual -> sortedWith(
            compareBy(EntryModel::position).thenByDescending(
                EntryModel::modifiedAt
            )
        )

        SettingsSortingType.IssuerAsc -> sortedBy { it.issuer.lowercase() }
        SettingsSortingType.IssuerDesc -> sortedByDescending { it.issuer.lowercase() }
    }

    private fun undecryptableEntriesWarningSnackbar() = SnackbarEvent(
        messageResId = sharedR.string.warning_undecryptable_entries,
        action = SnackbarEvent.Action(
            nameResId = sharedR.string.action_dismiss,
            onAction = {
                viewModelScope.launch {
                    updateSettingsUseCase(
                        settingsFlow.value.copy(isUndecryptableEntriesWarningDismissed = true)
                    )
                }
            }
        )
    )

    private companion object {

        private const val TAG = "HomeMasterViewModel"

        private const val SEARCH_QUERY_DEFAULT_VALUE = ""

        private const val SEARCH_QUERY_EMPTY_DEBOUNCE_MILLIS = 0L

        private const val SEARCH_QUERY_DEBOUNCE_MILLIS = 200L

    }

}
