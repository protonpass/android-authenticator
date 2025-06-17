plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.shared.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.core.utilKotlin)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.zxing.core)

    ksp(libs.hilt.compiler)
}
