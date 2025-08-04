plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.exports.passwords"
}

dependencies {
    implementation(projects.business.entries)
}
