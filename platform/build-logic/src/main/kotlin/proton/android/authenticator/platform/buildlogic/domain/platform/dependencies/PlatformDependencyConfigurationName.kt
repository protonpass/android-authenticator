package proton.android.authenticator.platform.buildlogic.domain.platform.dependencies

internal enum class PlatformDependencyConfigurationName(val value: String) {
    Api(value = "api"),
    DebugImplementation(value = "debugImplementation"),
    Implementation(value = "implementation"),
    Ksp(value = "ksp"),
}
