plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.features.shared"
}

androidComponents {
    beforeVariants { variant ->
        variant.enableAndroidTest = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.authenticator.common)
    implementation(libs.core.auth)
    implementation(libs.core.crypto)
    implementation(libs.core.featureFlag.domain)
    implementation(libs.hilt.android)

    implementation(projects.business.shared)
    implementation(projects.business.appLock)
    implementation(projects.business.backups)
    implementation(projects.business.biometrics)
    implementation(projects.business.keys)
    implementation(projects.business.entries)
    implementation(projects.business.settings)
    implementation(projects.business.steps)
    implementation(projects.business.users)
    implementation(projects.shared.common)
    implementation(projects.shared.crypto)

    ksp(libs.hilt.compiler)
}
