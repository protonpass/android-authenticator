plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.navigation"
}

dependencies {
    implementation(libs.androidx.material.navigation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.core.utilKotlin)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.business.steps)
    implementation(projects.features.backups.master)
    implementation(projects.features.exports.completion)
    implementation(projects.features.exports.errors)
    implementation(projects.features.home.manual)
    implementation(projects.features.home.master)
    implementation(projects.features.home.scan)
    implementation(projects.features.imports.completion)
    implementation(projects.features.imports.errors)
    implementation(projects.features.imports.options)
    implementation(projects.features.imports.passwords)
    implementation(projects.features.onboarding.biometrics)
    implementation(projects.features.onboarding.imports)
    implementation(projects.features.onboarding.master)
    implementation(projects.features.settings.master)
    implementation(projects.features.shared)
    implementation(projects.features.sync.disable)
    implementation(projects.features.sync.master)
    implementation(projects.shared.common)
    implementation(projects.shared.ui)

    ksp(libs.hilt.compiler)
}
