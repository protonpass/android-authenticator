plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.sync.disable"
}

dependencies {
    implementation(projects.business.entries)
    implementation(projects.business.settings)
    implementation(projects.business.users)

    implementation(projects.features.sync.shared)
}
