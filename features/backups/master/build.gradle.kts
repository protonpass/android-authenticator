plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.backups.master"
}

dependencies {
    implementation(projects.business.backups)
    implementation(projects.business.entries)
}
