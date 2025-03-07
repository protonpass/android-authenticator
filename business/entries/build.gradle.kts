plugins {
    id("proton.android.authenticator.plugins.libraries.business")
}

android {
    namespace = "proton.android.authenticator.business.entries"
}

dependencies {
    implementation(libs.kotlinx.datetime)
}
