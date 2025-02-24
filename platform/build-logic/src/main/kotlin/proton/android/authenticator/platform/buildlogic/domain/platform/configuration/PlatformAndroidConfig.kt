package proton.android.authenticator.platform.buildlogic.domain.platform.configuration

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal object PlatformAndroidConfig {

    internal const val APPLICATION_ID: String = "proton.android.authenticator"

    internal const val COMPILE_SDK: Int = 34

    internal const val EXCLUDED_PACKAGING_RESOURCES: String = "/META-INF/{AL2.0,LGPL2.1}"

    internal const val MIN_SDK: Int = 24

    internal const val NAMESPACE: String = "proton.android.authenticator"

    internal const val TARGET_SDK: Int = 34

    internal const val USES_COMPOSE: Boolean = true

    internal const val VERSION_CODE: Int = 1

    internal const val VERSION_NAME: String = "1.0"

    internal val CompileJavaVersion: JavaVersion = JavaVersion.VERSION_17

    internal val CompileJvmTarget: JvmTarget = JvmTarget.JVM_17

    internal object BuildTypes {

        internal object Debug {

            internal const val APPLICATION_ID_SUFFIX: String = ".debug"

            internal const val NAME: String = "debug"

            internal const val IS_MINIFY_ENABLED: Boolean = false

            internal const val IS_SHRINK_RESOURCES: Boolean = false

        }

        internal object Release {

            internal const val APPLICATION_ID_SUFFIX: String = ""

            internal const val NAME: String = "release"

            internal const val IS_MINIFY_ENABLED: Boolean = true

            internal const val IS_SHRINK_RESOURCES: Boolean = true

        }
    }

}
