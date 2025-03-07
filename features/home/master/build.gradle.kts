plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.home.master"
}

dependencies {
    implementation(project(":business:entries"))
}
