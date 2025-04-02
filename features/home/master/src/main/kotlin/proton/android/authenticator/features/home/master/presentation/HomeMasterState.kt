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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.models.UiTextMask

internal class HomeMasterState private constructor(
    private val entryModelsMap: Map<String, HomeMasterEntryModel>
) {

    internal val entryModels: List<HomeMasterEntryModel>
        get() = entryModelsMap.values.toList()

    internal val hasEntryModels: Boolean = entryModels.isNotEmpty()

    internal companion object {

        @Composable
        internal fun create(
            entriesFlow: Flow<List<Entry>>,
            entryCodesFlow: Flow<List<EntryCode>>,
            entryCodesRemainingTimesFlow: Flow<Map<Int, Int>>,
            entrySearchQueryFlow: Flow<String>,
            settingsFlow: Flow<Settings>
        ): HomeMasterState {
            val entries by entriesFlow.collectAsState(emptyList())
            val entryCodes by entryCodesFlow.collectAsState(emptyList())
            val entryCodesRemainingTimes by entryCodesRemainingTimesFlow.collectAsState(emptyMap())
            val entrySearchQuery by entrySearchQueryFlow.collectAsState("")
            val settings by settingsFlow.collectAsState(Settings.Default)

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
                    SettingsDigitType.Plain -> false
                    SettingsDigitType.Rich -> true
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
                entries
//                    .filter { entry ->
//                        if (entrySearchQuery.isEmpty()) true
//                        else entry.name.contains(entrySearchQuery, ignoreCase = true)
//                    }
                    .zip(entryCodes) { entry, entryCode ->
                        HomeMasterEntryModel(
                            id = entry.id,
                            name = UiText.Dynamic(value = entry.name),
                            issuer = UiText.Dynamic(value = entry.issuer),
                            currentCode = UiText.Dynamic(
                                value = entryCode.currentCode,
                                masks = listOf(UiTextMask.Totp)
                            ),
                            nextCode = UiText.Dynamic(
                                value = entryCode.nextCode,
                                masks = listOf(UiTextMask.Totp)
                            ),
                            remainingSeconds = entryCodesRemainingTimes.getOrDefault(
                                key = entry.period,
                                defaultValue = 0
                            ),
                            totalSeconds = entry.period,
                            animateOnCodeChange = animateOnCodeChange,
                            showShadowsInTexts = showShadowsInTexts,
                            showBoxesInCode = showBoxesInCode
                        )
                    }.associateBy { entryModel -> entryModel.id }
            }

            return HomeMasterState(entryModelsMap = entryModelsMap)
        }

    }

}
