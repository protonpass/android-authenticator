plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.exports.errors"
}

dependencies {
    implementation(projects.business.entries)
}
