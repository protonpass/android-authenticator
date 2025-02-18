package proton.android.authenticator.platform.buildlogic.domain.platform.dependencies

internal enum class PlatformDependencyModule(val value: String) {
    BusinessShared(":business:shared"),
    SharedInfrastructure(":shared:infrastructure"),
    SharedUi(":shared:ui"),
}
