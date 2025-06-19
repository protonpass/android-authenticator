plugins {
    id("proton.android.authenticator.plugins.libraries.business")
}

android {
    namespace = "proton.android.authenticator.business.users"
}

dependencies {
    implementation(libs.core.accountManager)
}
