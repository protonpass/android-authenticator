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

package proton.android.authenticator.business.shared.telemetry

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.telemetry.domain.TelemetryManager
import me.proton.core.telemetry.domain.entity.TelemetryEvent
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthenticatorTelemetryManagerImpl @Inject constructor(
    private val accountManager: AccountManager,
    private val telemetryManager: TelemetryManager
) : AuthenticatorTelemetryManager {

    override suspend fun sendEvent(event: AuthenticatorTelemetryEvent) {
        runCatching {
            val userId = accountManager.getPrimaryUserId().firstOrNull()
            val coreEvent = event.toCoreEvent()
            AuthenticatorLogger.d(TAG, "Enqueueing telemetry event: $coreEvent (userId=$userId)")
            telemetryManager.enqueue(userId, coreEvent)
        }.onFailure { throwable ->
            if (throwable is CancellationException) throw throwable
            AuthenticatorLogger.w(TAG, throwable, "Failed to send telemetry event")
        }
    }

    private fun AuthenticatorTelemetryEvent.toCoreEvent(): TelemetryEvent = when (this) {
        is AuthenticatorTelemetryEvent.CreateEntry -> TelemetryEvent(
            group = TELEMETRY_GROUP,
            name = "create_entry",
            dimensions = mapOf("create_source" to source.value)
        )
        is AuthenticatorTelemetryEvent.CopyCode -> TelemetryEvent(
            group = TELEMETRY_GROUP,
            name = "copy_code"
        )
        is AuthenticatorTelemetryEvent.RemoveEntry -> TelemetryEvent(
            group = TELEMETRY_GROUP,
            name = "remove_entry"
        )
        is AuthenticatorTelemetryEvent.ReorderEntry -> TelemetryEvent(
            group = TELEMETRY_GROUP,
            name = "reorder_entry"
        )
        is AuthenticatorTelemetryEvent.Import -> TelemetryEvent(
            group = TELEMETRY_GROUP,
            name = "import",
            dimensions = mapOf("import_source" to source.value),
            values = mapOf("entries_count" to entriesCount.toFloat())
        )
        is AuthenticatorTelemetryEvent.Export -> TelemetryEvent(
            group = TELEMETRY_GROUP,
            name = "export"
        )
        is AuthenticatorTelemetryEvent.OpenApp -> TelemetryEvent(
            group = TELEMETRY_GROUP,
            name = "open_app"
        )
    }

    companion object {
        private const val TAG = "AuthenticatorTelemetryManagerImpl"
        private const val TELEMETRY_GROUP = "authenticator.any.user_actions"
    }
}
