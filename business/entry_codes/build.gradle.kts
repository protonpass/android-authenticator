plugins {
    id("proton.android.authenticator.plugins.libraries.business")
}

android {
    namespace = "proton.android.authenticator.business.entrycodes"
}

dependencies {
    implementation(libs.kotlinx.datetime)
}
