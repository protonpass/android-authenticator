plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.settings.master"
}

dependencies {
    implementation(project(":business:proton_apps"))
    implementation(project(":business:settings"))
}
