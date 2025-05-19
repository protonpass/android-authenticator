plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.biometrics.shared"
}

dependencies {
    implementation(libs.androidx.biometric)

    implementation(project(":business:biometrics"))
}
