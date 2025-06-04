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

package proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.steps

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import proton.android.authenticator.proto.preferences.steps.StepPreferences
import java.io.InputStream
import java.io.OutputStream

internal object StepProtoPreferencesSerializer : Serializer<StepPreferences> {

    override val defaultValue: StepPreferences = StepPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StepPreferences = try {
        StepPreferences.parseFrom(input)
    } catch (error: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read StepPreferences proto.", error)
    }

    override suspend fun writeTo(stepPreferences: StepPreferences, output: OutputStream) {
        stepPreferences.writeTo(output)
    }

}
