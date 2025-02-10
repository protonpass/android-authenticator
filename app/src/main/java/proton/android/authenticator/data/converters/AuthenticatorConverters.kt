package proton.android.authenticator.data.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import proton.android.authenticator.domain.EntryType

class AuthenticatorConverters {

    @TypeConverter
    fun fromInstant(value: Instant): Long = value.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(value: Long): Instant = Instant.fromEpochMilliseconds(value)

    @TypeConverter
    fun fromEntryType(value: EntryType?): String? = value?.name

    @TypeConverter
    fun toEntryType(value: String?): EntryType? = value?.let { EntryType.valueOf(it) }
}
