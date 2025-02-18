plugins {
    `kotlin-dsl`
}

group = "proton.android.authenticator.platform.build_logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.gradle.android)
    compileOnly(libs.gradle.kotlin)
}

gradlePlugin {
    plugins {
        register("library-android") {
            id = "proton.android.authenticator.plugins.libraries.android"
            implementationClass = "proton.android.authenticator.platform.buildlogic.plugins.AndroidLibraryConventionPlugin"
        }


        register("library-feature") {
            id = "proton.android.authenticator.plugins.libraries.feature"
            implementationClass = "proton.android.authenticator.platform.buildlogic.plugins.FeatureLibraryConventionPlugin"
        }
    }
}
