import com.google.protobuf.gradle.id
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("proton.android.authenticator.plugins.libraries.android")

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "proton.android.authenticator.business.shared"

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

protobuf {
    protoc {
        artifact = project.libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }

                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val capName = variant.name.capitalized()

            tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
                setSource(tasks.getByName("generate${capName}Proto").outputs)
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.room.ktx)
    implementation(libs.core.account)
    implementation(libs.core.accountManager)
    implementation(libs.core.accountManager.presentation.compose)
    implementation(libs.core.accountRecovery)
    implementation(libs.core.auth)
    implementation(libs.core.authFidoDagger)
    implementation(libs.core.authFidoDomain)
    implementation(libs.core.biometric)
    implementation(libs.core.challenge)
    implementation(libs.core.config.data)
    implementation(libs.core.country)
    implementation(libs.core.crypto)
    implementation(libs.core.cryptoValidator)
    implementation(libs.core.data)
    implementation(libs.core.dataRoom)
    implementation(libs.core.deviceMigration)
    implementation(libs.core.domain)
    implementation(libs.core.eventManager)
    implementation(libs.core.featureFlag)
    implementation(libs.core.featureFlag.domain)
    implementation(libs.core.humanVerification)
    implementation(libs.core.key)
    implementation(libs.core.keyTransparency)
    implementation(libs.core.network)
    implementation(libs.core.notification)
    implementation(libs.core.observability)
    implementation(libs.core.passValidator)
    implementation(libs.core.payment)
    implementation(libs.core.plan)
    implementation(libs.core.presentation)
    implementation(libs.core.presentation.compose)
    implementation(libs.core.push)
    implementation(libs.core.report)
    implementation(libs.core.telemetry.data)
    implementation(libs.core.telemetry.domain)
    implementation(libs.core.telemetry.presentation)
    implementation(libs.core.user)
    implementation(libs.core.userRecovery)
    implementation(libs.core.userSettings)
    implementation(libs.core.utilAndroidDagger)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.protobuf.lite)
    implementation(projects.shared.common)

    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
}
