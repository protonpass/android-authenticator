plugins {
    id("proton.android.authenticator.plugins.libraries.feature")
}

android {
    namespace = "proton.android.authenticator.features.home.master"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

androidComponents {
    beforeVariants { variant ->
        variant.enableAndroidTest = false
    }
}

dependencies {
    implementation(projects.business.entries)
    implementation(projects.business.entryCodes)
    implementation(projects.business.settings)

    implementation(projects.business.backups)
    implementation(projects.business.appLock)
    implementation(projects.business.shared)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
