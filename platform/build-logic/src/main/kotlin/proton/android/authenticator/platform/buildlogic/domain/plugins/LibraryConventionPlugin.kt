package proton.android.authenticator.platform.buildlogic.domain.plugins

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import proton.android.authenticator.platform.buildlogic.domain.platform.configuration.PlatformAndroidConfig

internal abstract class LibraryConventionPlugin : ConventionPlugin() {

    protected fun Project.configureAndroidEnvironment() {
        extensions.configure<LibraryExtension> {
            compileSdk = PlatformAndroidConfig.COMPILE_SDK

            defaultConfig {
                minSdk = PlatformAndroidConfig.MIN_SDK
            }

            compileOptions {
                sourceCompatibility = PlatformAndroidConfig.CompileJavaVersion
                targetCompatibility = PlatformAndroidConfig.CompileJavaVersion
            }
        }
    }

}
