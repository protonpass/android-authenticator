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

import android.net.Uri
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.entries.application.shared.constants.EntryConstants
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileReader
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorImportException
import proton.android.authenticator.commonrust.AuthenticatorImporterInterface
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.models.MimeType
import proton.android.authenticator.shared.common.domain.providers.MimeTypeProvider
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import proton.android.authenticator.shared.common.domain.scanners.QrScanner
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.tags.EncryptionTag
import javax.inject.Inject

internal class EntriesImporter @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val authenticatorImporter: AuthenticatorImporterInterface,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val fileReader: FileReader,
    private val mimeTypeProvider: MimeTypeProvider,
    private val qrScanner: QrScanner,
    private val repository: EntriesRepository,
    private val timeProvider: TimeProvider
) {

    internal suspend fun import(
        contentUris: List<Uri>,
        importType: EntryImportType,
        password: String?
    ): Int = saveEntries(getEntries(contentUris, importType, password))

    private suspend fun getEntries(
        contentUris: List<Uri>,
        importType: EntryImportType,
        password: String?
    ): List<AuthenticatorEntryModel> = contentUris.flatMap { contentUri ->
        var content = ""
        var contentBinary: ByteArray = byteArrayOf()

        when (importType) {
            EntryImportType.ProtonPass ->
                contentBinary = fileReader.readBinary(contentUri.toString(), MAX_ZIP_SIZE)

            EntryImportType.Google -> content = qrScanner.scan(contentUri).orEmpty()

            else -> content = fileReader.readText(contentUri.toString())
        }
        if (content.isEmpty()) {
            assert(contentBinary.isNotEmpty()) { "Content binary must exist" }
        } else {
            assert(contentBinary.isEmpty()) { "Content binary must not exist" }
        }
        getEntriesFromContent(importType, contentUri, content, contentBinary, password)
    }

    private suspend fun getEntriesFromContent(
        importType: EntryImportType,
        contentUri: Uri,
        content: String,
        contentBinary: ByteArray,
        password: String?
    ) = withContext(appDispatchers.default) {
        when (importType) {
            EntryImportType.Aegis -> {
                authenticatorImporter.importFromAegisJson(content, password)
            }

            EntryImportType.Bitwarden -> {
                mimeTypeProvider.getFileMimeType(contentUri.toString()).let { mimeType ->
                    when (mimeType) {
                        MimeType.CommaSeparatedValues,
                        MimeType.Csv -> authenticatorImporter.importFromBitwardenCsv(content)

                        MimeType.Json -> authenticatorImporter.importFromBitwardenJson(content)
                        MimeType.All,
                        MimeType.Binary,
                        MimeType.Image,
                        MimeType.Text,
                        MimeType.Zip -> {
                            throw IllegalArgumentException("Unsupported Bitwarden file type: $mimeType")
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

            EntryImportType.ProtonAuthenticator -> {
                authenticatorImporter.importFromProtonAuthenticator(content)
            }

            EntryImportType.ProtonPass -> {
                authenticatorImporter.importFromPassZip(contentBinary)
            }

            EntryImportType.TwoFas -> {
                authenticatorImporter.importFrom2fas(content, password)
            }

            EntryImportType.Authy,
            EntryImportType.Microsoft -> {
                throw IllegalArgumentException("Unsupported import type: $importType")
            }
        }.let { result ->
            if (result.errors.isNotEmpty()) {
                throw AuthenticatorImportException.BadContent(result.errors.first().message)
            }
            result.entries
        }
    }

    private suspend fun saveEntries(entryModels: List<AuthenticatorEntryModel>): Int {
        var position = repository.searchMaxPosition()

        return encryptionContextProvider.withEncryptionContext {
            entryModels.map { entryModel ->
                position += EntryConstants.POSITION_INCREMENT

                Entry(
                    id = entryModel.id,
                    content = encrypt(
                        authenticatorClient.serializeEntry(entryModel),
                        EncryptionTag.EntryContent
                    ),
                    modifiedAt = timeProvider.currentSeconds(),
                    isDeleted = false,
                    isSynced = false,
                    position = position
                )
            }
        }
            .also { entries -> repository.saveAll(entries) }
            .let(List<Entry>::size)
    }

    private companion object {
        const val MAX_ZIP_SIZE = 10 * 1_024 * 1_024
    }
}
