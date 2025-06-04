plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.home.manual"
}

dependencies {
    implementation(projects.business.entries)
}
