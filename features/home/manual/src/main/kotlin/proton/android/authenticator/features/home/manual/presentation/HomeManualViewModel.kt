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

package proton.android.authenticator.features.home.manual.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.application.create.CreateEntryReason
import proton.android.authenticator.business.entries.application.update.UpdateEntryReason
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.R
import proton.android.authenticator.features.home.manual.usecases.CreateEntryUseCase
import proton.android.authenticator.features.home.manual.usecases.UpdateEntryUseCase
import proton.android.authenticator.features.shared.entries.usecases.GetEntryModelUseCase
import proton.android.authenticator.features.shared.usecases.snackbars.DispatchSnackbarEventUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import javax.inject.Inject

@HiltViewModel
internal class HomeManualViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getEntryModelUseCase: GetEntryModelUseCase,
    private val createEntryUseCase: CreateEntryUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase,
    private val dispatchSnackbarEventUseCase: DispatchSnackbarEventUseCase
) : ViewModel() {

    private val entryId: String? = savedStateHandle[ARGS_ENTRY_ID]

    private val entryModelFlow = flow {
        entryId
            ?.let { id -> getEntryModelUseCase(id) }
            .also { entry -> emit(entry) }
    }

    private val eventFlow = MutableStateFlow<HomeManualEvent>(value = HomeManualEvent.Idle)

    private val titleState = mutableStateOf<String?>(value = null)

    private val isValidTitleFlow = MutableStateFlow<Boolean>(value = true)

    private val secretState = mutableStateOf<String?>(value = null)

    private val isValidSecretFlow = MutableStateFlow<Boolean>(value = true)

    private val issuerState = mutableStateOf<String?>(value = null)

    private val digitsFlow = MutableStateFlow<Int?>(value = null)

    private val timeIntervalFlow = MutableStateFlow<Int?>(value = null)

    private val algorithmFlow = MutableStateFlow<EntryAlgorithm?>(value = null)

    private val typeFlow = MutableStateFlow<EntryType?>(value = null)

    private val showAdvanceOptionsFlow = MutableStateFlow<Boolean?>(value = null)

    private val formTextInputsFlow = combine(
        snapshotFlow { titleState.value },
        snapshotFlow { secretState.value },
        snapshotFlow { issuerState.value },
        ::HomeManualTextInputs
    )

    private val formInputsFlow: Flow<HomeManualInputs> = combine(
        digitsFlow,
        timeIntervalFlow,
        algorithmFlow,
        typeFlow,
        formTextInputsFlow,
        ::HomeManualInputs
    )

    private val formModelFlow = combine(
        entryModelFlow,
        formInputsFlow,
        showAdvanceOptionsFlow,
        isValidSecretFlow,
        isValidTitleFlow
    ) { entryModel, formInputs, showAdvanceOptions, isValidSecret, isValidTitle ->
        if (entryModel == null) {
            HomeManualFormModel(
                title = formInputs.title.orEmpty(),
                secret = formInputs.secret.orEmpty(),
                issuer = formInputs.issuer.orEmpty(),
                digits = formInputs.digits ?: DEFAULT_DIGITS,
                timeInterval = formInputs.timeInterval ?: DEFAULT_TIME_INTERVAL,
                algorithm = formInputs.algorithm ?: DEFAULT_ALGORITHM,
                type = formInputs.type ?: DEFAULT_TYPE,
                position = 0.0,
                showAdvanceOptions = showAdvanceOptions == true,
                isValidSecret = isValidSecret,
                isValidTitle = isValidTitle,
                mode = HomeManualMode.Create
            )
        } else {
            HomeManualFormModel(
                title = formInputs.title ?: entryModel.name,
                secret = formInputs.secret ?: entryModel.secret,
                issuer = formInputs.issuer ?: entryModel.issuer,
                digits = formInputs.digits ?: entryModel.digits,
                timeInterval = formInputs.timeInterval ?: entryModel.period,
                algorithm = formInputs.algorithm ?: entryModel.algorithm,
                type = formInputs.type ?: entryModel.type,
                position = entryModel.position,
                showAdvanceOptions = showAdvanceOptions == true,
                isValidSecret = isValidSecret,
                isValidTitle = isValidTitle,
                mode = HomeManualMode.Edit
            )
        }
    }

    internal val stateFlow: StateFlow<HomeManualState> = combine(
        eventFlow,
        formModelFlow,
        HomeManualState::Ready
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeManualState.Loading
    )

    internal fun onEventConsumed(event: HomeManualEvent) {
        eventFlow.compareAndSet(expect = event, update = HomeManualEvent.Idle)
    }

    internal fun onTitleChange(newTitle: String) {
        titleState.value = newTitle

        isValidTitleFlow.update { true }
    }

    internal fun onSecretChange(newSecret: String) {
        secretState.value = newSecret.trim()

        isValidSecretFlow.update { true }
    }

    internal fun onIssuerChange(newIssuer: String) {
        issuerState.value = newIssuer.trimStart()
    }

    internal fun onDigitsChange(newDigits: Int) {
        digitsFlow.update { newDigits }
    }

    internal fun onTimeIntervalChange(newTimeInterval: Int) {
        timeIntervalFlow.update { newTimeInterval }
    }

    internal fun onAlgorithmChange(newAlgorithm: EntryAlgorithm) {
        algorithmFlow.update { newAlgorithm }
    }

    internal fun onTypeChange(newType: EntryType) {
        typeFlow.update { newType }

        showAdvanceOptionsFlow.update { true }
    }

    internal fun onShowAdvanceOptions() {
        showAdvanceOptionsFlow.update { true }
    }

    internal fun onSubmitForm(formModel: HomeManualFormModel) {
        if (entryId == null) {
            createEntry(formModel)
        } else {
            updateEntry(entryId, formModel)
        }
    }

    private fun createEntry(formModel: HomeManualFormModel) {
        viewModelScope.launch {
            createEntryUseCase(formModel = formModel).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        when (answer.reason) {
                            CreateEntryReason.CannotSaveEntry -> {
                                dispatchSnackbarEvent(
                                    messageResId = R.string.home_manual_snackbar_message_create_error
                                )
                            }

                            CreateEntryReason.InvalidEntryTitle -> {
                                isValidTitleFlow.update { false }
                            }

                            CreateEntryReason.InvalidEntrySecret -> {
                                isValidSecretFlow.update { false }
                            }

                            CreateEntryReason.Unknown -> {}
                        }
                    }

                    is Answer.Success -> {
                        eventFlow.update { HomeManualEvent.OnEntryCreated }
                    }
                }
            }
        }
    }

    private fun updateEntry(entryId: String, formModel: HomeManualFormModel) {
        viewModelScope.launch {
            updateEntryUseCase(entryId = entryId, formModel = formModel).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        when (answer.reason) {
                            UpdateEntryReason.EntryNotFound -> {
                                dispatchSnackbarEvent(
                                    messageResId = R.string.home_manual_snackbar_message_update_error
                                )
                            }

                            UpdateEntryReason.InvalidEntrySecret -> {
                                isValidSecretFlow.update { false }
                            }
                        }
                    }

                    is Answer.Success -> {
                        eventFlow.update { HomeManualEvent.OnEntryUpdated }
                    }
                }
            }
        }
    }

    private fun dispatchSnackbarEvent(@StringRes messageResId: Int) {
        viewModelScope.launch {
            SnackbarEvent(messageResId = messageResId)
                .also { event -> dispatchSnackbarEventUseCase(snackbarEvent = event) }
        }
    }

    private companion object {

        private const val ARGS_ENTRY_ID = "entryId"

        private const val DEFAULT_DIGITS = 6

        private const val DEFAULT_TIME_INTERVAL = 30

        private val DEFAULT_ALGORITHM = EntryAlgorithm.SHA1

        private val DEFAULT_TYPE = EntryType.TOTP

    }

}
