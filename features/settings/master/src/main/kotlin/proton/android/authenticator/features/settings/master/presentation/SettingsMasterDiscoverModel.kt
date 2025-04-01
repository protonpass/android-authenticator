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

package proton.android.authenticator.features.settings.master.presentation

import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.protonapps.domain.ProtonApp
import proton.android.authenticator.protonapps.domain.ProtonAppType
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.R as uiR

internal data class SettingsMasterDiscoverModel(private val uninstalledProtonApps: List<ProtonApp>) {

    private val discoverProtonAppsMap = mapOf(
        ProtonAppType.Calendar to Triple(
            first = UiIcon.Resource(id = uiR.drawable.ic_logo_calendar),
            second = UiText.Resource(id = R.string.settings_discover_calendar_title),
            third = UiText.Resource(id = R.string.settings_discover_calendar_description)
        ),
        ProtonAppType.Drive to Triple(
            first = UiIcon.Resource(id = uiR.drawable.ic_logo_drive),
            second = UiText.Resource(id = R.string.settings_discover_drive_title),
            third = UiText.Resource(id = R.string.settings_discover_drive_description)
        ),
        ProtonAppType.Mail to Triple(
            first = UiIcon.Resource(id = uiR.drawable.ic_logo_mail),
            second = UiText.Resource(id = R.string.settings_discover_mail_title),
            third = UiText.Resource(id = R.string.settings_discover_mail_description)
        ),
        ProtonAppType.Pass to Triple(
            first = UiIcon.Resource(id = uiR.drawable.ic_logo_pass),
            second = UiText.Resource(id = R.string.settings_discover_pass_title),
            third = UiText.Resource(id = R.string.settings_discover_pass_description)
        ),
        ProtonAppType.Vpn to Triple(
            first = UiIcon.Resource(id = uiR.drawable.ic_logo_vpn),
            second = UiText.Resource(id = R.string.settings_discover_vpn_title),
            third = UiText.Resource(id = R.string.settings_discover_vpn_description)
        )
    )

    internal val shouldShowDiscoverSection: Boolean = uninstalledProtonApps.isNotEmpty()

    internal val discoverProtonApps: List<SettingsMasterDiscoverApp> = uninstalledProtonApps
        .mapNotNull { protonApp ->
            discoverProtonAppsMap[protonApp.type]?.let { (icon, title, description) ->
                SettingsMasterDiscoverApp(
                    id = protonApp.id,
                    icon = icon,
                    title = title,
                    description = description
                )
            }
        }

}
