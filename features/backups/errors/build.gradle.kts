plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.backups.errors"
}

dependencies {
    implementation(projects.business.backups)
}
