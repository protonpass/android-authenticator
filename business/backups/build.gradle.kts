plugins {
    id("proton.android.authenticator.plugins.libraries.business")
}

android {
    namespace = "proton.android.authenticator.business.backups"
}

dependencies {
    implementation(libs.document.file)
}
