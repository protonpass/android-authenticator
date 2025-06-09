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
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.shared.ui.domain.models.UiTextMask
import proton.android.authenticator.shared.ui.domain.theme.ThemeType

@Immutable
internal sealed interface HomeMasterState {

    val searchQuery: String

    val showBottomBar: Boolean

    val showFabButton: Boolean

    val showTopSearchBar: Boolean

    @Immutable
    data object Empty : HomeMasterState {

        override val searchQuery: String = ""

        override val showBottomBar: Boolean = false

        override val showFabButton: Boolean = false

        override val showTopSearchBar: Boolean = false

    }

    @Immutable
    data object Loading : HomeMasterState {

        override val searchQuery: String = ""

        override val showBottomBar: Boolean = false

        override val showFabButton: Boolean = false

        override val showTopSearchBar: Boolean = false

    }

    @Immutable
    data class Ready(
        override val searchQuery: String,
        internal val animateOnCodeChange: Boolean,
        internal val showBoxesInCode: Boolean,
        internal val themeType: ThemeType,
        internal val entryCodeMasks: List<UiTextMask>,
        internal val entryModelsMap: Map<String, HomeMasterEntryModel>,
        internal val areCodesHidden: Boolean,
        private val entryCodesRemainingTimes: Map<Int, Int>,
        private val searchBarType: SettingsSearchBarType
    ) : HomeMasterState {

        override val showBottomBar: Boolean = searchBarType == SettingsSearchBarType.Bottom

        override val showTopSearchBar: Boolean = searchBarType == SettingsSearchBarType.Top

        override val showFabButton: Boolean = showTopSearchBar && searchQuery.isEmpty()

        internal val entryModels: List<HomeMasterEntryModel> = entryModelsMap.values.toList()

        internal fun getRemainingSeconds(totalSeconds: Int): Int = entryCodesRemainingTimes.getOrDefault(
            key = totalSeconds,
            defaultValue = 0
        )

    }

    companion object {

        @Composable
        internal fun create(
            entrySearchQuery: String,
            entryModelsFlow: Flow<List<EntryModel>>,
            entryCodesFlow: Flow<List<EntryCode>>,
            entryCodesRemainingTimesFlow: Flow<Map<Int, Int>>,
            settingsFlow: Flow<Settings>
        ): HomeMasterState {
            val entriesList: List<EntryModel>? by entryModelsFlow.collectAsState(initial = null)
            val entryCodes by entryCodesFlow.collectAsState(initial = emptyList())
            val entryCodesRemainingTimes by entryCodesRemainingTimesFlow.collectAsState(initial = emptyMap())
            val settings by settingsFlow.collectAsState(initial = Settings.Default)

            return entriesList?.let { entries ->
                if (entries.isEmpty()) {
                    Empty
                } else {
                    val areCodesHidden = remember(key1 = settings.isHideCodesEnabled) {
                        settings.isHideCodesEnabled
                    }

                    val animateOnCodeChange = remember(key1 = settings.isCodeChangeAnimationEnabled) {
                        settings.isCodeChangeAnimationEnabled
                    }

                    val themeType = remember(key1 = settings.themeType) {
                        when (settings.themeType) {
                            SettingsThemeType.Dark -> ThemeType.Dark
                            SettingsThemeType.Light -> ThemeType.Light
                            SettingsThemeType.System -> ThemeType.System
                        }
                    }

                    val showBoxesInCode = remember(key1 = settings.digitType) {
                        when (settings.digitType) {
                            SettingsDigitType.Boxes -> true
                            SettingsDigitType.Plain -> false
                        }
                    }

                    val entryCodeMasks = remember(key1 = areCodesHidden) {
                        buildList {
                            if (areCodesHidden) {
                                add(UiTextMask.Hidden)
                            }
                            add(UiTextMask.Totp)
                        }
                    }

                    val entryModelsMap = remember(key1 = entries, key2 = entryCodes) {
                        entries.zip(entryCodes) { entry, entryCode ->
                            HomeMasterEntryModel(
                                entryModel = entry,
                                entryCode = entryCode
                            )
                        }.associateBy { entryModel -> entryModel.id }
                    }

                    Ready(
                        animateOnCodeChange = animateOnCodeChange,
                        searchQuery = entrySearchQuery,
                        showBoxesInCode = showBoxesInCode,
                        areCodesHidden = areCodesHidden,
                        themeType = themeType,
                        entryCodeMasks = entryCodeMasks,
                        entryModelsMap = entryModelsMap,
                        entryCodesRemainingTimes = entryCodesRemainingTimes,
                        searchBarType = settings.searchBarType
                    )
                }
            } ?: Loading
        }
    }
}
