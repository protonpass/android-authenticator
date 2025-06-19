plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.sync.master"
}

dependencies {
    implementation(libs.core.accountManager)

    implementation(projects.business.settings)
}
