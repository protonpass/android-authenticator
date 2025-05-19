plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.onboarding.biometrics"
}

dependencies {
    implementation(project(":business:biometrics"))
    implementation(project(":business:settings"))
}
