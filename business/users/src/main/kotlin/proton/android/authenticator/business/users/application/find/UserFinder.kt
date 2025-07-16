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

package proton.android.authenticator.business.users.application.find

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.proton.core.account.domain.entity.Account
import me.proton.core.account.domain.entity.isReady
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.accountmanager.domain.getPrimaryAccount
import me.proton.core.user.domain.repository.UserRepository
import proton.android.authenticator.business.users.domain.User
import javax.inject.Inject

internal class UserFinder @Inject constructor(
    private val accountManager: AccountManager,
    private val userRepository: UserRepository
) {

    internal fun find(): Flow<User?> = accountManager.getPrimaryAccount()
        .map { primaryAccount ->
            primaryAccount
                ?.takeIf(Account::isReady)
                ?.let { account -> userRepository.getUser(sessionUserId = account.userId) }
                ?.let { accountUser ->
                    User(
                        id = accountUser.userId.id,
                        email = accountUser.email,
                        username = accountUser.name
                    )
                }
        }

}
