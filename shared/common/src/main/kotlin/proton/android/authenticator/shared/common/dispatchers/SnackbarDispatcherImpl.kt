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

package proton.android.authenticator.shared.common.dispatchers

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import proton.android.authenticator.shared.common.domain.dispatchers.SnackbarDispatcher
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import javax.inject.Inject

internal class SnackbarDispatcherImpl @Inject constructor() : SnackbarDispatcher {

    private val eventsChannel = Channel<SnackbarEvent>()

    override suspend fun dispatch(event: SnackbarEvent) {
        eventsChannel.send(event)
    }

    override fun observe(): Flow<SnackbarEvent> = eventsChannel.receiveAsFlow()

}
