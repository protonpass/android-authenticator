plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.backups.master"
}

androidComponents {
    beforeVariants { variant ->
        variant.enableAndroidTest = false
    }
}

dependencies {
    implementation(libs.core.crypto)
    implementation(libs.core.utilKotlin)

    implementation(projects.business.backups)
    implementation(projects.business.entries)
}
