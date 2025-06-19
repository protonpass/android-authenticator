import studio.forface.easygradle.dsl.implementation

plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.sync.errors"
}

dependencies {
    implementation(projects.features.sync.shared)
}
