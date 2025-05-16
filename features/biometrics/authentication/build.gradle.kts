plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.biometrics.authentication"
}

dependencies {
    implementation(libs.androidx.biometric)

    implementation(project(":features:biometrics:shared"))
}
