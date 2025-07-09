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

package proton.android.authenticator.features.imports.onboarding.presentation

import androidx.compose.runtime.Stable
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.onboarding.R
import proton.android.authenticator.shared.common.domain.constants.UrlConstants
import proton.android.authenticator.shared.common.domain.models.MimeType
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.R as uiR

@Stable
internal data class ImportOnboardingState(
    internal val event: ImportOnboardingEvent,
    private val importType: EntryImportType
) {

    internal val helpUrl: String = UrlConstants.CUSTOMER_SUPPORT

    internal val isMultiSelectionAllowed: Boolean = when (importType) {
        EntryImportType.Google -> true
        EntryImportType.Aegis,
        EntryImportType.Authy,
        EntryImportType.Bitwarden,
        EntryImportType.Ente,
        EntryImportType.LastPass,
        EntryImportType.Microsoft,
        EntryImportType.ProtonAuthenticator,
        EntryImportType.ProtonPass,
        EntryImportType.TwoFas -> false
    }

    internal val isSupported: Boolean = importType.isSupported

    internal val mimeTypes: List<String> = importType.mimeTypes.map(MimeType::value)

    internal val providerIcon: UiIcon = when (importType) {
        EntryImportType.Aegis -> uiR.drawable.ic_authenticator_aegis
        EntryImportType.Authy -> uiR.drawable.ic_authenticator_authy
        EntryImportType.Bitwarden -> uiR.drawable.ic_authenticator_bitwarden
        EntryImportType.Ente -> uiR.drawable.ic_authenticator_ente
        EntryImportType.Google -> uiR.drawable.ic_authenticator_google
        EntryImportType.LastPass -> uiR.drawable.ic_authenticator_lastpass
        EntryImportType.Microsoft -> uiR.drawable.ic_authenticator_microsoft
        EntryImportType.ProtonAuthenticator -> uiR.drawable.ic_authenticator_proton_authenticator
        EntryImportType.ProtonPass -> uiR.drawable.ic_authenticator_proton_pass
        EntryImportType.TwoFas -> uiR.drawable.ic_authenticator_2fas
    }.let(UiIcon::Resource)

    internal val providerNameText: UiText = when (importType) {
        EntryImportType.Aegis -> uiR.string.authenticator_aegis
        EntryImportType.Authy -> uiR.string.authenticator_authy
        EntryImportType.Bitwarden -> uiR.string.authenticator_bitwarden
        EntryImportType.Ente -> uiR.string.authenticator_ente
        EntryImportType.Google -> uiR.string.authenticator_google
        EntryImportType.LastPass -> uiR.string.authenticator_last_pass
        EntryImportType.Microsoft -> uiR.string.authenticator_microsoft
        EntryImportType.ProtonAuthenticator -> uiR.string.authenticator_proton_authenticator
        EntryImportType.ProtonPass -> uiR.string.authenticator_proton_pass
        EntryImportType.TwoFas -> uiR.string.authenticator_2fas
    }.let(UiText::Resource)

    internal val providerStepsResId: Int = when (importType) {
        EntryImportType.Aegis -> R.array.imports_onboarding_aegis_steps_v2
        EntryImportType.Bitwarden -> R.array.imports_onboarding_bitwarden_steps_v2
        EntryImportType.Ente -> R.array.imports_onboarding_ente_steps_v2
        EntryImportType.Google -> R.array.imports_onboarding_google_steps_v2
        EntryImportType.LastPass -> R.array.imports_onboarding_lastpass_steps_v2
        EntryImportType.ProtonAuthenticator -> R.array.imports_onboarding_proton_authenticator_steps_v2
        EntryImportType.ProtonPass -> R.array.imports_onboarding_proton_pass_steps
        EntryImportType.TwoFas -> R.array.imports_onboarding_2fas_steps_v2
        EntryImportType.Authy,
        EntryImportType.Microsoft -> R.array.imports_onboarding_unsupported_steps
    }

}
