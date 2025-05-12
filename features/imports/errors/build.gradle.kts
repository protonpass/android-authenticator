plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.imports.errors"
}

dependencies {
    implementation(project(":business:entries"))
}
