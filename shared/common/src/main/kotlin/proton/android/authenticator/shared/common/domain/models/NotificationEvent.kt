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

package proton.android.authenticator.shared.common.domain.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import proton.android.authenticator.shared.common.R

sealed interface NotificationEvent {

    enum class Topic(
        internal val channelId: String,
        @StringRes internal val channelNameResId: Int,
        internal val notificationId: Int
    ) {
        Backups(
            channelId = TOPIC_BACKUPS_CHANNEL_ID,
            channelNameResId = R.string.notification_channel_name_automatic_backups,
            notificationId = TOPIC_BACKUPS_NOTIFICATION_ID
        )
    }

    @get:DrawableRes
    val iconResId: Int

    val title: String

    val text: String

    val topic: Topic

    data class Informative(
        @DrawableRes override val iconResId: Int,
        override val title: String,
        override val text: String,
        override val topic: Topic
    ) : NotificationEvent

    private companion object {

        private const val TOPIC_BACKUPS_CHANNEL_ID = "backups_notification_channel_id"

        private const val TOPIC_BACKUPS_NOTIFICATION_ID = 1

    }

}
