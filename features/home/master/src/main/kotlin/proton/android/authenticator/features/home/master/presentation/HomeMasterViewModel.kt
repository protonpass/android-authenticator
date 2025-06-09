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
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.features.home.master.R
import proton.android.authenticator.features.home.master.usecases.DeleteEntryUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntryCodesUseCase
import proton.android.authenticator.features.home.master.usecases.RearrangeEntryUseCase
import proton.android.authenticator.features.home.master.usecases.RestoreEntryUseCase
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
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
    private val timeProvider: TimeProvider
) : ViewModel() {

    private val entrySearchQueryState = mutableStateOf<String>(value = SEARCH_QUERY_DEFAULT_VALUE)

    @OptIn(FlowPreview::class)
    private val entrySearchQueryDebouncedFlow = snapshotFlow { entrySearchQueryState.value }
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
                entryModel.issuer.contains(searchQuery, true) || entryModel.name.contains(searchQuery, true)
            }
        }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    private val entryCodesFlow = entryModelsFlow
        .flatMapLatest(observeEntryCodesUseCase::invoke)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
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
    ) { entries, entryCodes, _ ->
        entries.associate { entry ->
            entry.period to timeProvider.remainingPeriodSeconds(entry.period)
        }
    }

    internal val stateFlow: StateFlow<HomeMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        HomeMasterState.create(
            entrySearchQuery = entrySearchQueryState.value,
            entryModelsFlow = entryModelsFlow,
            entryCodesFlow = entryCodesFlow,
            entryCodesRemainingTimesFlow = entryCodesRemainingTimesFlow,
            settingsFlow = observeSettingsUseCase()
        )
    }

    internal fun onCopyEntryCode(entry: HomeMasterEntryModel, areCodesHidden: Boolean) {
        copyToClipboardUseCase(text = entry.currentCode, isSensitive = areCodesHidden)
    }

    internal fun onDeleteEntry(entry: HomeMasterEntryModel) {
        viewModelScope.launch {
            deleteEntryUseCase(id = entry.id).also { answer ->
                when (answer) {
                    is Answer.Failure -> println("JIBIRI: Delete entry failed -> ${answer.reason}")
                    is Answer.Success -> {
                        SnackbarEvent(
                            messageResId = R.string.home_snackbar_message_entry_deleted,
                            action = SnackbarEvent.Action(
                                nameResId = uiR.string.action_undo,
                                onAction = { restoreEntry(entry = answer.data) }
                            )
                        ).also { snackbarEvent ->
                            dispatchSnackbarEventUseCase(snackbarEvent)
                        }
                    }
                }
            }
        }
    }

    private fun restoreEntry(entry: Entry) {
        viewModelScope.launch {
            restoreEntryUseCase(entry).also { answer ->
                when (answer) {
                    is Answer.Failure -> println("JIBIRI: Restore entry failed -> ${answer.reason}")
                    is Answer.Success -> Unit
                }
            }
        }
    }

    internal fun onRearrangeEntry(
        fromEntryId: String,
        fromEntryIndex: Int,
        toEntryId: String,
        toEntryIndex: Int,
        entryModelsMap: Map<String, HomeMasterEntryModel>
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
                    is Answer.Failure -> println("JIBIRI: Rearrange entry failed -> ${answer.reason}")
                    is Answer.Success -> Unit
                }
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
