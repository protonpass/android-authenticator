plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.qa.master"
}

dependencies {
    implementation(projects.business.settings)
}
