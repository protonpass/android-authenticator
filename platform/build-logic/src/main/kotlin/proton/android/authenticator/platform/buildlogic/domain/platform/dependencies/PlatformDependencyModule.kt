package proton.android.authenticator.platform.buildlogic.domain.platform.dependencies

internal enum class PlatformDependencyModule(val value: String) {
    BusinessShared(":business:shared"),
    FeaturesShared(":features:shared"),
    SharedCommon(":shared:common"),
    SharedCrypto(":shared:crypto"),
    SharedUi(":shared:ui")
}
