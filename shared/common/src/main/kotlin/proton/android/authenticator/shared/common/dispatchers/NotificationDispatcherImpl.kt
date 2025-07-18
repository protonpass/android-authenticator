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

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import proton.android.authenticator.shared.common.domain.dispatchers.NotificationDispatcher
import proton.android.authenticator.shared.common.domain.models.NotificationEvent
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import javax.inject.Inject

internal class NotificationDispatcherImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManagerCompat: NotificationManagerCompat
) : NotificationDispatcher {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun dispatch(event: NotificationEvent) {
        if (!hasNotificationPermission()) return

        event
            .also(::createNotificationChannel)
            .let(::buildNotification)
            .also { notification -> showNotification(event, notification) }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createNotificationChannel(event: NotificationEvent) {
        with(event.topic) {
            NotificationChannel(
                channelId,
                context.getString(channelNameResId),
                NotificationManager.IMPORTANCE_HIGH
            )
        }.also(notificationManagerCompat::createNotificationChannel)
    }

    private fun buildNotification(event: NotificationEvent) = with(event) {
        when (this) {
            is NotificationEvent.Informative -> NotificationCompat.Builder(
                context,
                topic.channelId
            )
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(event: NotificationEvent, notification: Notification) {
        try {
            notificationManagerCompat.notify(event.topic.notificationId, notification)
        } catch (exception: SecurityException) {
            AuthenticatorLogger.w(TAG, "Cannot show notifications: tried to notify without permission")
            AuthenticatorLogger.w(TAG, exception)
        }
    }

    private companion object {

        private const val TAG = "NotificationDispatcher"

    }

}
