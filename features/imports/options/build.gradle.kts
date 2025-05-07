plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.imports.options"
}

dependencies {
    implementation(project(":business:entries"))
}
