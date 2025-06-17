plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.shared.ui"
}

dependencies {
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.core.presentation.compose)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.reorderable)
    implementation(libs.zxing.core)
    implementation(platform(libs.androidx.compose.bom))

    ksp(libs.hilt.compiler)
}
