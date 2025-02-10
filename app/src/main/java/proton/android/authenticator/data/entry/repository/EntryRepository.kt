package proton.android.authenticator.data.entry.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import me.proton.core.crypto.common.keystore.EncryptedByteArray
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.crypto.EncryptionContextProvider
import proton.android.authenticator.data.entry.EntryEntity
import proton.android.authenticator.data.entry.local.EntryLocalDataSource
import proton.android.authenticator.domain.Entry
import proton.android.authenticator.domain.EntryType
import javax.inject.Inject

interface EntryRepository {
    fun observeAllEntries(): Flow<List<Entry>>
    suspend fun insertUri(uri: String)
}

class EntryRepositoryImpl @Inject constructor(
    private val rustAuthenticatorClient: AuthenticatorMobileClientInterface,
    private val localDataSource: EntryLocalDataSource,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val clock: Clock
) : EntryRepository {

    override fun observeAllEntries(): Flow<List<Entry>> = localDataSource.observeAllEntries()
        .map { entities: List<EntryEntity> ->
            val decrypted: List<ByteArray> =
                encryptionContextProvider.withEncryptionContextSuspendable {
                    entities.map { decrypt(it.content) }
                }
            val models: List<AuthenticatorEntryModel> =
                rustAuthenticatorClient.deserializeEntries(decrypted)
            if (entities.size != models.size) {
                throw IllegalStateException("The number of deserialized models does not match the number of entities")
            }
            entities.zip(models) { entity, model ->
                Entry(
                    id = entity.id,
                    model = model,
                    createdAt = entity.createdAt,
                    modifiedAt = entity.modifiedAt
                )
            }
        }

    override suspend fun insertUri(uri: String) {
        val entry: AuthenticatorEntryModel = rustAuthenticatorClient.entryFromUri(uri)
        val encrypted: EncryptedByteArray = encryptionContextProvider.withEncryptionContext {
            encrypt(entry.actions.serialize())
        }

        val now = clock.now()
        val entity = EntryEntity(
            content = encrypted,
            type = EntryType.TOTP, // update lib and get it as an enum from entry.entrytype
            createdAt = now,
            modifiedAt = now
        )
        localDataSource.insertEntry(entity)
    }
}
