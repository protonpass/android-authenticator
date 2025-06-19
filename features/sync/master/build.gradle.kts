plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.sync.master"
}

dependencies {
    implementation(projects.business.settings)

    implementation(projects.features.sync.shared)
}
