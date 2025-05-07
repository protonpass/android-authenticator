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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":features:home:manual"))
    implementation(project(":features:home:master"))
    implementation(project(":features:home:scan"))
    implementation(project(":features:imports:options"))
    implementation(project(":features:onboarding:biometrics"))
    implementation(project(":features:onboarding:imports"))
    implementation(project(":features:onboarding:master"))
    implementation(project(":features:settings:master"))
    implementation(project(":shared:ui"))

    ksp(libs.hilt.compiler)
}
