plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.features.imports.shared"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)

    implementation(projects.business.entries)
    implementation(projects.shared.common)

    ksp(libs.hilt.compiler)
}
