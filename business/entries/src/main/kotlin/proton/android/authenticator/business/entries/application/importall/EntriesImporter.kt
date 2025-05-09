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

package proton.android.authenticator.business.entries.application.importall

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorImportException
import proton.android.authenticator.commonrust.AuthenticatorImporterInterface
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.models.MimeType
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import java.io.BufferedReader
import javax.inject.Inject

internal class EntriesImporter @Inject constructor(
    private val contentResolver: ContentResolver,
    private val appDispatchers: AppDispatchers,
    private val authenticatorImporter: AuthenticatorImporterInterface,
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val timeProvider: TimeProvider,
    private val repository: EntriesRepository
) {

    internal suspend fun import(
        contentUri: Uri,
        importType: EntryImportType,
        password: String?
    ): Int = getFileContent(contentUri)
        ?.let { content -> getEntriesFromContent(importType, contentUri, content, password) }
        ?.let { entryModels -> saveEntries(entryModels) }
        ?: throw AuthenticatorImportException.BadContent("Cannot read file content")

    private suspend fun getFileContent(uri: Uri) = withContext(appDispatchers.io) {
        contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use(BufferedReader::readText)
    }

    private suspend fun getEntriesFromContent(
        importType: EntryImportType,
        contentUri: Uri,
        content: String,
        password: String?
    ) = withContext(appDispatchers.default) {
        when (importType) {
            EntryImportType.Aegis -> {
                authenticatorImporter.importFromAegisJson(content, password)
            }

            EntryImportType.Bitwarden -> {
                contentResolver.getType(contentUri).let { mimeTypeValue ->
                    when (MimeType.fromValue(mimeTypeValue.orEmpty())) {
                        MimeType.CommaSeparatedValues,
                        MimeType.Csv -> authenticatorImporter.importFromBitwardenCsv(content)

                        MimeType.Json -> authenticatorImporter.importFromBitwardenJson(content)
                        MimeType.All,
                        MimeType.Binary,
                        MimeType.Image,
                        MimeType.Text -> {
                            throw IllegalArgumentException("Unsupported Bitwarden file type: $mimeTypeValue")
                        }
                    }
                }
            }

            EntryImportType.Ente -> {
                authenticatorImporter.importFromEnteTxt(content)
            }

            EntryImportType.Google -> {
                authenticatorImporter.importFromGoogleQr(content)
            }

            EntryImportType.LastPass -> {
                authenticatorImporter.importFromLastpassJson(content)
            }

            EntryImportType.Proton -> {
                authenticatorImporter.importFromProtonAuthenticator(content)
            }

            EntryImportType.TwoFas -> {
                authenticatorImporter.importFrom2fas(content, password)
            }
        }.let { result ->
            if (result.errors.isNotEmpty()) {
                throw AuthenticatorImportException.BadContent(result.errors.first().message)
            }
            result.entries
        }
    }

    private suspend fun saveEntries(entryModels: List<AuthenticatorEntryModel>): Int = timeProvider.currentMillis()
        .let { currentMillis ->
            entryModels.map { entryModel ->
                Entry(
                    model = entryModel,
                    params = authenticatorClient.getTotpParams(entryModel),
                    createdAt = currentMillis,
                    modifiedAt = currentMillis
                )
            }
        }
        .also { entries -> repository.saveAll(entries) }
        .let(List<Entry>::size)

}
