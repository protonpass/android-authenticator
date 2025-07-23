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

package proton.android.authenticator.features.backups.master.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.backups.master.R
import proton.android.authenticator.features.backups.master.presentation.BackupsMasterState
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.shared.common.domain.constants.CharacterConstants
import proton.android.authenticator.shared.ui.domain.components.buttons.SecondaryActionButton
import proton.android.authenticator.shared.ui.domain.components.containers.RowsContainer
import proton.android.authenticator.shared.ui.domain.components.rows.NavigationRow
import proton.android.authenticator.shared.ui.domain.components.rows.SelectorRow
import proton.android.authenticator.shared.ui.domain.components.rows.ToggleRow
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundSection
import proton.android.authenticator.shared.ui.domain.screens.AlertDialogScreen
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun BackupsMasterContent(
    state: BackupsMasterState,
    onDisableBackup: () -> Unit,
    onFolderPicked: (Uri) -> Unit,
    onFrequencyChange: (BackupFrequencyType) -> Unit,
    onBackupNowClick: (List<EntryModel>) -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    var showNotificationsExplanationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let { folderUri ->
            context.contentResolver
                .takePersistableUriPermission(
                    folderUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                .also { onFolderPicked(folderUri) }
        }
    }

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return@remember mutableStateOf(true)
        } else {
            val allowed = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            return@remember mutableStateOf(allowed)
        }
    }

    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            folderLauncher.launch(backupModel.directoryUri)
        }
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Large)
    ) {
        RowsContainer(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundSection(),
            contents = buildList {
                add(
                    {
                        ToggleRow(
                            titleText = UiText.Resource(id = R.string.backups_automatic_backups_title),
                            isChecked = backupModel.isEnabled,
                            onCheckedChange = { isEnablingBackup ->
                                if (isEnablingBackup) {
                                    if (hasNotificationPermission) {
                                        folderLauncher.launch(backupModel.directoryUri)
                                    } else {
                                        showNotificationsExplanationDialog = true
                                    }
                                } else {
                                    onDisableBackup()
                                }
                            }
                        )
                    }
                )
                if (backupModel.isEnabled) {
                    add(
                        {
                            NavigationRow(
                                titleText = UiText.Resource(R.string.backups_automatic_backups_location_title),
                                description = UiText.Dynamic(
                                    DocumentsContract.getTreeDocumentId(
                                        backupModel.directoryUri
                                    ).substringAfter(CharacterConstants.COLON)
                                ),
                                showNavigationIcon = true,
                                onClick = {
                                    folderLauncher.launch(backupModel.directoryUri)
                                }
                            )
                        }
                    )
                    add(
                        {
                            SelectorRow(
                                titleText = UiText.Resource(id = R.string.backups_frequency_title),
                                options = backupModel.frequencyOptions,
                                onSelectedOptionChange = onFrequencyChange
                            )
                        }
                    )
                }
            }
        )

        if (backupModel.isEnabled) {
            if (canCreateBackup) {
                SecondaryActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.backups_backup_now_button),
                    onClick = { onBackupNowClick(entryModels) }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall)
            ) {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.backups_backup_count_description,
                        count = backupModel.maxBackupCount,
                        backupModel.maxBackupCount
                    ),
                    style = Theme.typography.captionRegular,
                    color = Theme.colorScheme.textWeak
                )

                backupModel.lastBackupDate?.let { date ->
                    Text(
                        text = stringResource(
                            id = R.string.backups_last_backup_description,
                            date.asString()
                        ),
                        style = Theme.typography.captionRegular,
                        color = Theme.colorScheme.textWeak
                    )
                }
            }
        }
    }

    if (showNotificationsExplanationDialog) {
        AlertDialogScreen(
            title = UiText.Resource(id = R.string.backups_notifications_permission_dialog_title),
            message = UiText.Resource(id = R.string.backups_notifications_permission_dialog_explanation),
            confirmText = UiText.Resource(id = R.string.backups_notifications_permission_dialog_allow),
            cancelText = UiText.Resource(id = R.string.backups_notifications_permission_dialog_deny),
            onCancellation = {
                showNotificationsExplanationDialog = false
                folderLauncher.launch(backupModel.directoryUri)
            },
            onConfirmation = {
                showNotificationsExplanationDialog = false
                permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        )
    }
}
