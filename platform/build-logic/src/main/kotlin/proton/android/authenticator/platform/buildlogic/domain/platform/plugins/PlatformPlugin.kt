package proton.android.authenticator.platform.buildlogic.domain.platform.plugins

internal enum class PlatformPlugin(val value: String) {
    AndroidApplication(value = "com.android.application"),
    AndroidLibrary(value = "com.android.library"),
    DaggerHilt(value = "dagger.hilt.android.plugin"),
    DropboxDependencyGuard(value = "com.dropbox.dependency-guard"),
    KotlinAndroid(value = "org.jetbrains.kotlin.android"),
    KotlinCompose(value = "org.jetbrains.kotlin.plugin.compose"),
    KotlinSerialization(value = "org.jetbrains.kotlin.plugin.serialization"),
    Ksp(value = "com.google.devtools.ksp"),
    ProtonEnvironmentConfig(value = "me.proton.core.gradle-plugins.environment-config"),
    Sentry(value = "io.sentry.android.gradle")
}
