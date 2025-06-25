plugins {
    id("proton.android.authenticator.plugins.libraries.business")
}

android {
    namespace = "proton.android.authenticator.business.keys"
}

dependencies {
    compileOnly(files("../../../proton-libs/gopenpgp/gopenpgp.aar"))

    implementation(libs.core.accountManager)
}
