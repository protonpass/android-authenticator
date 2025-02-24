package proton.android.authenticator.platform.buildlogic.domain.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import proton.android.authenticator.platform.buildlogic.domain.platform.configuration.PlatformAndroidConfig
import proton.android.authenticator.platform.buildlogic.domain.platform.configuration.PlatformVersionCatalogsConfig
import proton.android.authenticator.platform.buildlogic.domain.platform.dependencies.PlatformDependencyBundle
import proton.android.authenticator.platform.buildlogic.domain.platform.dependencies.PlatformDependencyConfigurationName
import proton.android.authenticator.platform.buildlogic.domain.platform.dependencies.PlatformDependencyModule
import proton.android.authenticator.platform.buildlogic.domain.platform.plugins.PlatformPlugin

internal abstract class ConventionPlugin : Plugin<Project> {

    protected fun PluginAware.applyPlugin(plugin: PlatformPlugin) {
        apply(plugin = plugin.value)
    }

    protected fun Project.addBundleDependency(
        bundle: PlatformDependencyBundle,
        configurationName: PlatformDependencyConfigurationName,
    ) {
        getVersionCatalogsBundle(bundle.value)
            .also { versionCatalogsBundle ->
                addDependency(configurationName, versionCatalogsBundle)
            }
    }

    protected fun Project.addModuleDependency(
        module: PlatformDependencyModule,
        configurationName: PlatformDependencyConfigurationName,
    ) {
        addDependency(configurationName, project(module.value))
    }

    protected fun Project.configureKotlinOptions() {
        tasks.withType(KotlinJvmCompile::class) {
            compilerOptions {
                jvmTarget.set(PlatformAndroidConfig.CompileJvmTarget)
            }
        }
    }

    private fun Project.getVersionCatalogsBundle(bundleName: String) = getVersionCatalogs()
        .findBundle(bundleName)
        .get()

    private fun Project.getVersionCatalogsLibrary(libraryName: String) = getVersionCatalogs()
        .findLibrary(libraryName)
        .get()

    private fun Project.getVersionCatalogs() = extensions
        .getByType<VersionCatalogsExtension>()
        .named(PlatformVersionCatalogsConfig.NAME)

    private fun Project.addDependency(
        configurationName: PlatformDependencyConfigurationName,
        dependencyNotation: Any,
    ) {
        dependencies.add(configurationName.value, dependencyNotation)
    }

}
