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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.shared.ui.domain.models.UiTextMask

@Immutable
internal sealed interface HomeMasterState {

    val hasEntryModels: Boolean

    @Immutable
    data object Empty : HomeMasterState {

        override val hasEntryModels: Boolean = false

    }

    @Immutable
    data object Loading : HomeMasterState {

        override val hasEntryModels: Boolean = false

    }

    @Immutable
    data class Loaded(
        internal val animateOnCodeChange: Boolean,
        internal val showShadowsInTexts: Boolean,
        internal val showBoxesInCode: Boolean,
        private val entryModelsMap: Map<String, HomeMasterEntryModel>
    ) : HomeMasterState {

        internal val entryModels: List<HomeMasterEntryModel>
            get() = entryModelsMap.values.toList()

        override val hasEntryModels: Boolean = entryModels.isNotEmpty()

    }

    companion object {

        @Composable
        internal fun create(
            entriesFlow: Flow<List<Entry>>,
            entryCodesFlow: Flow<List<EntryCode>>,
            entryCodesRemainingTimesFlow: Flow<Map<Int, Int>>,
            entrySearchQueryFlow: Flow<String>,
            settingsFlow: Flow<Settings>
        ): HomeMasterState {
            val entries: List<Entry>? by entriesFlow.collectAsState(initial = null)
            val entryCodes by entryCodesFlow.collectAsState(emptyList())
            val entryCodesRemainingTimes by entryCodesRemainingTimesFlow.collectAsState(emptyMap())
            val entrySearchQuery by entrySearchQueryFlow.collectAsState("")
            val settings by settingsFlow.collectAsState(Settings.Default)

            if (entries == null) {
                return Loading
            }

            if (entries!!.isEmpty()) {
                return Empty
            }

            val hideCodes = remember(key1 = settings.isHideCodesEnabled) {
                settings.isHideCodesEnabled
            }

            val animateOnCodeChange = remember(key1 = settings.isCodeChangeAnimationEnabled) {
                settings.isCodeChangeAnimationEnabled
            }

            val showShadowsInTexts = remember(key1 = settings.themeType) {
                when (settings.themeType) {
                    SettingsThemeType.System -> false
                    SettingsThemeType.Light -> false
                    SettingsThemeType.Dark -> true
                }
            }

            val showBoxesInCode = remember(key1 = settings.digitType) {
                when (settings.digitType) {
                    SettingsDigitType.Boxes -> true
                    SettingsDigitType.Plain -> false
                }
            }

            val codeMasks = remember(key1 = hideCodes) {
                buildList {
                    if (hideCodes) {
                        add(UiTextMask.Hidden)
                    }
                    add(UiTextMask.Totp)
                }
            }

            val entryModelsMap = remember(
                keys = arrayOf(
                    entrySearchQuery,
                    entries,
                    entryCodes,
                    entryCodesRemainingTimes,
                    animateOnCodeChange,
                    showShadowsInTexts,
                    showBoxesInCode
                )
            ) {
                entries!!.zip(entryCodes) { entry, entryCode ->
                    HomeMasterEntryModel(
                        entry = entry,
                        entryCode = entryCode,
                        entryCodesRemainingTimes = entryCodesRemainingTimes,
                        codeMasks = codeMasks
                    )
                }
                    .filter { entryModel -> entryModel.shouldBeShown(entrySearchQuery) }
                    .associateBy { entryModel -> entryModel.id }
            }

            return Loaded(
                animateOnCodeChange = animateOnCodeChange,
                showShadowsInTexts = showShadowsInTexts,
                showBoxesInCode = showBoxesInCode,
                entryModelsMap = entryModelsMap
            )
        }
    }
}
