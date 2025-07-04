plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.settings.master"
}

dependencies {
    implementation(projects.business.appLock)
    implementation(projects.business.biometrics)
    implementation(projects.business.entries)
    implementation(projects.business.protonApps)
    implementation(projects.business.settings)
    implementation(projects.business.users)
}
