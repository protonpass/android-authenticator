plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.imports.passwords"
}

dependencies {
    implementation(project(":business:entries"))
    implementation(project(":features:imports:shared"))
}
