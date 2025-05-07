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

import proton.android.authenticator.commonrust.AuthenticatorImportException
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import java.io.FileNotFoundException
import javax.inject.Inject

@Suppress("SwallowedException")
internal class ImportEntriesCommandHandler @Inject constructor(
    private val importer: EntriesImporter
) : CommandHandler<ImportEntriesCommand> {

    override suspend fun handle(command: ImportEntriesCommand) {
        try {
            importer.import(contentUri = command.contentUri, importType = command.importType)
        } catch (error: AuthenticatorImportException.BadContent) {
            println("JIBIRI: import handler error: BadContent")
        } catch (error: AuthenticatorImportException.BadPassword) {
            println("JIBIRI: import handler error: BadPassword")
        } catch (error: AuthenticatorImportException.DecryptionFailed) {
            println("JIBIRI: import handler error: DecryptionFailed")
        } catch (error: AuthenticatorImportException.MissingPassword) {
            println("JIBIRI: import handler error: MissingPassword")
        } catch (error: AuthenticatorImportException) {
            println("JIBIRI: import handler error: Unknown")
        } catch (error: FileNotFoundException) {
            println("JIBIRI: import handler error: FileNotFoundException")
        } catch (error: IllegalArgumentException) {
            println("JIBIRI: import handler error: IllegalArgumentException")
        }
    }

}
