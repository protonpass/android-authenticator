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

package proton.android.authenticator.shared.common.domain.configs

import me.proton.core.account.domain.entity.AccountType
import me.proton.core.auth.presentation.HelpOptionHandler
import me.proton.core.domain.entity.AppStore
import me.proton.core.domain.entity.Product
import proton.android.authenticator.shared.common.domain.builds.BuildFlavor

interface AppConfig {

    val accountType: AccountType

    val applicationId: String

    val appStore: AppStore

    val buildFlavor: BuildFlavor

    val isDebug: Boolean

    val product: Product

    val versionName: String

    val helpOptionHandler: HelpOptionHandler

    val productOnlyPaidPlans: Boolean

    val supportSignupPaidPlans: Boolean

    val supportUpgradePaidPlans: Boolean

}
