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

package proton.android.authenticator.features.home.master.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.features.shared.entries.presentation.EntryModel

internal class HomeMasterEntryModelPreviewProvider :
    PreviewParameterProvider<Map<String, HomeMasterEntryModel>> {

    override val values: Sequence<Map<String, HomeMasterEntryModel>> = sequenceOf(sampleEntries)

    companion object {

        private const val TOTP_PERIOD_SECONDS: Int = 30

        private const val TOTP_REMAINING_SECONDS: Int = 18

        private const val TOTP_DIGITS: Int = 6

        internal val sampleEntries: Map<String, HomeMasterEntryModel> = listOf(
            buildEntry(
                id = "1",
                name = "personal@proton.me",
                issuer = "Proton Mail",
                code = EntryCode(currentCode = "123456", nextCode = "789012"),
                position = 0
            ),
            buildEntry(
                id = "2",
                name = "octocat",
                issuer = "GitHub",
                code = EntryCode(currentCode = "654321", nextCode = "210987"),
                position = 1
            ),
            buildEntry(
                id = "3",
                name = "alice@example.com",
                issuer = "Google",
                code = EntryCode(currentCode = "112233", nextCode = "445566"),
                position = 2
            )
        ).associateBy(HomeMasterEntryModel::id)

        internal val sampleRemainingTimes: Map<Int, Int> =
            mapOf(TOTP_PERIOD_SECONDS to TOTP_REMAINING_SECONDS)

        private fun buildEntry(
            id: String,
            name: String,
            issuer: String,
            code: EntryCode,
            position: Int
        ): HomeMasterEntryModel = HomeMasterEntryModel(
            entryModel = EntryModel(
                id = id,
                name = name,
                issuer = issuer,
                note = null,
                period = TOTP_PERIOD_SECONDS,
                secret = "",
                type = EntryType.TOTP,
                uri = "",
                algorithm = EntryAlgorithm.SHA1,
                digits = TOTP_DIGITS,
                iconUrl = null,
                position = position,
                createdAt = 0L,
                modifiedAt = 0L,
                isDeleted = false,
                isSynced = false
            ),
            entryCode = code
        )
    }
}
