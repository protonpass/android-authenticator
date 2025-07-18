plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.logs.master"
}

dependencies {
    implementation(projects.business.logs)
}
