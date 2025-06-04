plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.imports.options"
}

dependencies {
    implementation(projects.business.entries)
    implementation(projects.features.imports.shared)
}
