plugins {
    id("proton.android.authenticator.plugins.libraries.android")
}

android {
    namespace = "proton.android.authenticator.shared.ui"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.coil)
    implementation(libs.coil.compose)
}
