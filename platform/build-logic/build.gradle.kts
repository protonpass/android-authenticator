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
        register("application-authenticator") {
            id = "proton.android.authenticator.plugins.applications.authenticator"
            implementationClass = "proton.android.authenticator.platform.buildlogic.plugins.AuthenticatorApplicationConventionPlugin"
        }

        register("library-android") {
            id = "proton.android.authenticator.plugins.libraries.android"
            implementationClass = "proton.android.authenticator.platform.buildlogic.plugins.AndroidLibraryConventionPlugin"
        }

        register("library-business") {
            id = "proton.android.authenticator.plugins.libraries.business"
            implementationClass = "proton.android.authenticator.platform.buildlogic.plugins.BusinessLibraryConventionPlugin"
        }

        register("library-feature") {
            id = "proton.android.authenticator.plugins.libraries.feature"
            implementationClass = "proton.android.authenticator.platform.buildlogic.plugins.FeatureLibraryConventionPlugin"
        }
    }
}
