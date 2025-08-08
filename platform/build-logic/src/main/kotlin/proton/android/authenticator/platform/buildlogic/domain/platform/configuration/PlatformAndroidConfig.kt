package proton.android.authenticator.platform.buildlogic.domain.platform.configuration

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal object PlatformAndroidConfig {

    internal const val APPLICATION_ID: String = "proton.android.authenticator"

    internal const val COMPILE_SDK: Int = 35

    internal const val EXCLUDED_PACKAGING_RESOURCES: String = "/META-INF/{AL2.0,LGPL2.1}"

    internal const val MIN_SDK: Int = 27

    internal const val NAMESPACE: String = "proton.android.authenticator"

    internal const val NDK_VERSION: String = "28.1.13356709"

    internal const val TARGET_SDK: Int = 35

    internal const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"

    internal const val USES_BUILD_CONFIG: Boolean = true

    internal const val USES_COMPOSE: Boolean = true

    internal const val VERSION_NAME: String = "1.1.2"

    internal val VERSION_CODE: Int = VERSION_NAME.split('.')
        .map(String::toInt)
        .let { segment ->
            segment[0].times(10_000_000) + segment[1].times(100_000) + segment[2].times(1_000)
        }

    internal val AbiFilters: Set<String> = setOf("armeabi-v7a", "arm64-v8a", "x86_64")

    internal val CompileJavaVersion: JavaVersion = JavaVersion.VERSION_17

    internal val CompileJvmTarget: JvmTarget = JvmTarget.JVM_17

    internal val LinterDisableOptions: Set<String> = setOf("NullSafeMutableLiveData")

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
