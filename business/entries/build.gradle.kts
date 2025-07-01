plugins {
    id("proton.android.authenticator.plugins.libraries.business")
}

android {
    namespace = "proton.android.authenticator.business.entries"
}

dependencies {
    implementation(libs.core.accountManager)
    implementation(libs.core.network)
    implementation(libs.kotlinx.datetime)
}
