plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.biometrics.activation"
}

dependencies {
    implementation(libs.androidx.biometric)

    implementation(project(":features:biometrics:shared"))
}
