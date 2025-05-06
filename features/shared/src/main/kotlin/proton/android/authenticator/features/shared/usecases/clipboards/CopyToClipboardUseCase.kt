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

package proton.android.authenticator.features.shared.usecases.clipboards

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Build
import android.os.PersistableBundle
import javax.inject.Inject

class CopyToClipboardUseCase @Inject constructor(
    private val clipboardManager: ClipboardManager
) {

    operator fun invoke(text: String, isSensitive: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ClipDescription.EXTRA_IS_SENSITIVE
        } else {
            CLIPBOARD_EXTRA_IS_SENSITIVE_KEY_COMPAT
        }.let { key ->
            PersistableBundle().apply { putBoolean(key, isSensitive) }
        }.let { bundle ->
            ClipData.newPlainText(CLIPBOARD_LABEL, text).apply { description.extras = bundle }
        }.also(clipboardManager::setPrimaryClip)
    }

    private companion object {

        private const val CLIPBOARD_LABEL = "authenticator-contents"

        private const val CLIPBOARD_EXTRA_IS_SENSITIVE_KEY_COMPAT = "android.content.extra.IS_SENSITIVE"

    }

}
