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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.features.home.master.R
import proton.android.authenticator.features.home.master.usecases.DeleteEntryUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntryCodesUseCase
import proton.android.authenticator.features.home.master.usecases.RearrangeEntryUseCase
import proton.android.authenticator.features.home.master.usecases.RestoreEntryUseCase
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.entries.usecases.SyncEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.clipboards.CopyToClipboardUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.snackbars.DispatchSnackbarEventUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
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
    private val deleteEntryUseCase: DeleteEntryUseCase,
    private val dispatchSnackbarEventUseCase: DispatchSnackbarEventUseCase,
    private val rearrangeEntryUseCase: RearrangeEntryUseCase,
    private val restoreEntryUseCase: RestoreEntryUseCase,
    private val syncEntryModelsUseCase: SyncEntryModelsUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {

    private val entrySearchQueryState = mutableStateOf(value = SEARCH_QUERY_DEFAULT_VALUE)

    private val entrySearchQueryFlow = snapshotFlow { entrySearchQueryState.value }

    private val isRefreshingFlow = MutableStateFlow(value = false)

    private val screenModelFlow = combine(
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

    private val entryModelsFlow = combine(
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
        started = SharingStarted.WhileSubscribed()
    )

    private val entryCodesFlow = entryModelsFlow
        .flatMapLatest(observeEntryCodesUseCase::invoke)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed()
        )

    private val entryCodesRemainingTimeTickerFlow = flow {
        while (coroutineContext.isActive) {
            emit(Unit)

            delay(1.seconds)
        }
    }

    private val entryCodesRemainingTimesFlow = combine(
        entryModelsFlow,
        entryCodesFlow,
        entryCodesRemainingTimeTickerFlow
    ) { entries, _, _ ->
        entries.associate { entry ->
            entry.period to timeProvider.remainingPeriodSeconds(entry.period)
        }
    }

    internal val stateFlow: StateFlow<HomeMasterState> = combine(
        screenModelFlow,
        entryModelsFlow,
        entryCodesFlow,
        entryCodesRemainingTimesFlow,
        observeSettingsUseCase()
    ) { screenModel, entryModels, entryCodes, entryCodesRemainingTimes, settings ->
        when {
            screenModel.searchQuery.isEmpty() && entryModels.isEmpty() -> {
                HomeMasterState.Empty
            }

            entryModels.isEmpty() -> {
                HomeMasterState.EmptySearch(
                    searchQuery = screenModel.searchQuery,
                    settings = settings
                )
            }

            else -> {
                HomeMasterState.Ready(
                    searchQuery = screenModel.searchQuery,
                    isRefreshing = screenModel.isRefreshing,
                    entries = entryModels,
                    entryCodes = entryCodes,
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

    internal fun onCopyEntryCode(entry: HomeMasterEntryModel, areCodesHidden: Boolean) {
        copyToClipboardUseCase(text = entry.currentCode, isSensitive = areCodesHidden)
            .let { isSupported ->
                SnackbarEvent(messageResId = R.string.home_snackbar_message_entry_copied)
                    .takeIf { !isSupported }
            }
            ?.also { event ->
                viewModelScope.launch {
                    dispatchSnackbarEventUseCase(event)
                }
            }
    }

    internal fun onDeleteEntry(entry: HomeMasterEntryModel) {
        viewModelScope.launch {
            deleteEntryUseCase(id = entry.id).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        SnackbarEvent(messageResId = R.string.home_snackbar_message_entry_delete_failed)
                    }

                    is Answer.Success -> {
                        SnackbarEvent(
                            messageResId = R.string.home_snackbar_message_entry_deleted,
                            action = SnackbarEvent.Action(
                                nameResId = uiR.string.action_undo,
                                onAction = { restoreEntry(entry = answer.data) }
                            )
                        )
                    }
                }.also { snackbarEvent ->
                    dispatchSnackbarEventUseCase(snackbarEvent)
                }
            }
        }
    }

    private fun restoreEntry(entry: Entry) {
        viewModelScope.launch {
            restoreEntryUseCase(entry).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        SnackbarEvent(messageResId = R.string.home_snackbar_message_entry_restore_failed)
                    }

                    is Answer.Success -> null
                }?.also { event -> dispatchSnackbarEventUseCase(event) }
            }
        }
    }

    @Suppress("LongParameterList")
    internal fun onRearrangeEntry(
        fromEntryId: String,
        fromEntryIndex: Int,
        toEntryId: String,
        toEntryIndex: Int,
        entryModelsMap: Map<String, HomeMasterEntryModel>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            rearrangeEntryUseCase(
                fromEntryId = fromEntryId,
                fromEntryIndex = fromEntryIndex,
                toEntryId = toEntryId,
                toEntryIndex = toEntryIndex,
                entryModelsMap = entryModelsMap
            ).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        SnackbarEvent(messageResId = R.string.home_snackbar_message_entry_rearrange_failed)
                    }

                    is Answer.Success -> {
                        onComplete()
                        null
                    }
                }?.also { event -> dispatchSnackbarEventUseCase(event) }
            }
        }
    }

    internal fun onRefreshEntries(isSyncEnabled: Boolean) {
        viewModelScope.launch {
            isRefreshingFlow.update { true }

            if (!isSyncEnabled) {
                delay(timeMillis = 50)
                isRefreshingFlow.update { false }
                return@launch
            }

            syncEntryModelsUseCase().also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        // This will be implemented in the following MR
                    }

                    is Answer.Success -> {
                        // This will be implemented in the following MR
                    }
                }
            }.also {
                isRefreshingFlow.update { false }
            }
        }
    }

    internal fun onUpdateEntrySearchQuery(newSearchQuery: String) {
        entrySearchQueryState.value = newSearchQuery.trimStart()
    }

    private companion object {

        private const val SEARCH_QUERY_DEFAULT_VALUE = ""

        private const val SEARCH_QUERY_EMPTY_DEBOUNCE_MILLIS = 0L

        private const val SEARCH_QUERY_DEBOUNCE_MILLIS = 200L

    }

}
