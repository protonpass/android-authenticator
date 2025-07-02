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

package proton.android.authenticator.features.shared.entries.presentation

import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType

data class EntryModel(
    val id: String,
    val name: String,
    val issuer: String,
    val note: String?,
    val period: Int,
    val secret: String,
    val type: EntryType,
    val uri: String,
    val algorithm: EntryAlgorithm,
    val digits: Int,
    val iconUrl: String?,
    val position: Double,
    val modifiedAt: Long,
    val isDeleted: Boolean,
    val isSynced: Boolean
)
