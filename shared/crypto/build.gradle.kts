plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.shared.crypto"
}

dependencies {
    implementation(libs.core.crypto)
    implementation(libs.hilt.android)

    implementation(projects.shared.common)

    ksp(libs.hilt.compiler)
}
