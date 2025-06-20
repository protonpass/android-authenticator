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

package proton.android.authenticator.app.configs

import me.proton.core.account.domain.entity.AccountType
import me.proton.core.auth.presentation.DefaultHelpOptionHandler
import me.proton.core.auth.presentation.HelpOptionHandler
import me.proton.core.domain.entity.AppStore
import me.proton.core.domain.entity.Product
import proton.android.authenticator.BuildConfig
import proton.android.authenticator.shared.common.domain.builds.BuildFlavor
import proton.android.authenticator.shared.common.domain.configs.AppConfig
import javax.inject.Inject

internal class AuthenticatorAppConfig @Inject constructor() : AppConfig {

    override val accountType: AccountType = AccountType.External

    override val buildFlavor: BuildFlavor = BuildFlavor.from(BuildConfig.FLAVOR)

    override val appStore: AppStore = when (buildFlavor) {
        BuildFlavor.AlphaBlack,
        BuildFlavor.AlphaProd,
        BuildFlavor.DevBlack,
        BuildFlavor.DevProd,
        BuildFlavor.PlayBlack,
        BuildFlavor.PlayProd -> AppStore.GooglePlay

        BuildFlavor.FdroidBlack,
        BuildFlavor.FdroidProd -> AppStore.FDroid
    }

    override val helpOptionHandler: HelpOptionHandler = DefaultHelpOptionHandler()

    override val isDebug: Boolean = BuildConfig.DEBUG

    override val product: Product = Product.Pass

    override val productOnlyPaidPlans: Boolean = false

    override val supportSignupPaidPlans: Boolean = false

    override val supportUpgradePaidPlans: Boolean = true

    override val versionName: String = BuildConfig.VERSION_NAME

}
