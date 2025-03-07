plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.business.shared"
}

dependencies {
    implementation(libs.androidx.room.ktx)
    implementation(libs.core.crypto)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.datetime)

    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
}
