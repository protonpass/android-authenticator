plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.biometrics.deactivation"
}

dependencies {
    implementation(libs.androidx.biometric)

    implementation(project(":business:settings"))
    implementation(project(":features:biometrics:shared"))
}
