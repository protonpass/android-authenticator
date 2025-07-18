plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.shared.common"
}

dependencies {
    api(libs.core.utilKotlin)

    implementation(libs.androidx.core.ktx)
    implementation(libs.authenticator.common)
    implementation(libs.core.account)
    implementation(libs.core.auth)
    implementation(libs.core.data)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.timber)
    implementation(libs.zxing.core)

    ksp(libs.hilt.compiler)
}
