plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.onboarding.biometrics"
}

dependencies {
    implementation(projects.business.biometrics)
    implementation(projects.business.settings)
    implementation(projects.business.steps)
}
