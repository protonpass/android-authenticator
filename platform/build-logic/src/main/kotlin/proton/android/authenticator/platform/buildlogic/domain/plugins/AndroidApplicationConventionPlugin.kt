package proton.android.authenticator.platform.buildlogic.domain.plugins

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import proton.android.authenticator.platform.buildlogic.domain.platform.configuration.PlatformAndroidConfig
import proton.android.authenticator.platform.buildlogic.domain.platform.configuration.PlatformAndroidConfig.EXCLUDED_PACKAGING_RESOURCES

internal abstract class AndroidApplicationConventionPlugin : ConventionPlugin() {

    protected fun Project.configureAndroidApplication() {
        extensions.configure<BaseAppModuleExtension> {
            namespace = PlatformAndroidConfig.NAMESPACE
            compileSdk = PlatformAndroidConfig.COMPILE_SDK

            defaultConfig {
                applicationId = PlatformAndroidConfig.APPLICATION_ID
                minSdk = PlatformAndroidConfig.MIN_SDK
                targetSdk = PlatformAndroidConfig.TARGET_SDK
                versionCode = PlatformAndroidConfig.VERSION_CODE
                versionName = PlatformAndroidConfig.VERSION_NAME
                testInstrumentationRunner = PlatformAndroidConfig.TEST_INSTRUMENTATION_RUNNER

                ndk {
                    abiFilters += PlatformAndroidConfig.AbiFilters
                }
            }

            compileOptions {
                sourceCompatibility = PlatformAndroidConfig.CompileJavaVersion
                targetCompatibility = PlatformAndroidConfig.CompileJavaVersion
            }

            buildFeatures {
                buildConfig = PlatformAndroidConfig.USES_BUILD_CONFIG
                compose = PlatformAndroidConfig.USES_COMPOSE
            }

            lint {
                disable += PlatformAndroidConfig.LinterDisableOptions
            }

            packaging {
                resources {
                    excludes += EXCLUDED_PACKAGING_RESOURCES
                }
            }
        }
    }

}
