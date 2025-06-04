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
    implementation(libs.core.crypto)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.protobuf.lite)

    implementation(projects.shared.common)

    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
}
