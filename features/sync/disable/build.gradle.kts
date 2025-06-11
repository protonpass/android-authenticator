plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.sync.disable"
}

dependencies {
    implementation(projects.business.settings)
}
