plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.home.master"
}

dependencies {
    implementation(projects.business.entries)
    implementation(projects.business.entryCodes)
    implementation(projects.business.settings)
}
