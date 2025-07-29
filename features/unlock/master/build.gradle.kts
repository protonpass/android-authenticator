plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.unlock.master"
}

dependencies {
    implementation(projects.business.appLock)
    implementation(projects.business.biometrics)
}
