plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.features.shared"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)

    implementation(project(":business:biometrics"))
    implementation(project(":business:settings"))
    implementation(project(":business:steps"))
    implementation(project(":shared:common"))

    ksp(libs.hilt.compiler)
}
