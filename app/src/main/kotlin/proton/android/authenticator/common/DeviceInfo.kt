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

package proton.android.authenticator.common

import android.content.Context
import android.os.Build
import android.os.LocaleList
import proton.android.authenticator.BuildConfig

private const val TAG = "DEVICE_INFO"

fun deviceInfo(context: Context) {
    AuthenticatorLogger.i(TAG, "-----------------------------------------")
    AuthenticatorLogger.i(TAG, "PACKAGE:     ${context.packageName}")
    AuthenticatorLogger.i(
        TAG,
        "OS:          Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    )
    AuthenticatorLogger.i(TAG, "VERSION:     ${BuildConfig.VERSION_NAME}")
    AuthenticatorLogger.i(TAG, "DEVICE:      ${Build.MANUFACTURER} ${Build.MODEL}")
    AuthenticatorLogger.i(TAG, "FINGERPRINT: ${Build.FINGERPRINT}")
    AuthenticatorLogger.i(TAG, "ABI:         ${Build.SUPPORTED_ABIS.joinToString(",")}")
    AuthenticatorLogger.i(TAG, "LOCALE:      ${LocaleList.getDefault().toLanguageTags()}")
    AuthenticatorLogger.i(TAG, "-----------------------------------------")
}
