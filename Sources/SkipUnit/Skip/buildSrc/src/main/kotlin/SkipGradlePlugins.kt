import org.gradle.api.*
import org.gradle.api.initialization.*
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import java.io.File
import java.io.FileReader
import java.nio.charset.Charset
import java.util.Properties
import com.android.build.gradle.BaseExtension
import com.android.build.api.dsl.*
import org.gradle.kotlin.dsl.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.initialization.Settings

// the Skip.env configuration for a Skip app, with shared constants for Xcode and gradle
val skipEnvFilename = "Skip.env"

interface SkipBuildExtension {
    val appName: Property<String>
}

class SkipBuildPlugin : Plugin<Project> {
    private var project: Project? = null

    override fun apply(project: Project) {
        this.project = project
        val baseDir = project.rootDir.resolve("..")
        val env = loadSkipEnv(baseDir.resolve(skipEnvFilename))

        with(project) {
            extensions.create("skip", SkipBuildExtension::class)
            // extensions: ext, libs, testLibs, versionCatalogs, kotlin, kotlinTestRegistry, base, defaultArtifacts, sourceSets, reporting, javaToolchains, java, android, androidComponents, buildOutputs, skip

            //val libsExtension = extensions.getByName("libs")
            val appName = env.skipEnv("PRODUCT_NAME")
            val packageName = env.skipEnv("ANDROID_PACKAGE_NAME")
            val appModule = packageName + ":" + appName
            val activity = env.skipEnv("PRODUCT_BUNDLE_IDENTIFIER") + "/" + packageName + ".MainActivity"
            dependencies.add("implementation", appModule)

            //val androidExtension = extensions.getByName("android") as com.android.build.gradle.BaseExtension // Unable to load class 'com.android.build.gradle.BaseExtension'
            // use a Groovy builder, which enables building up the DSL with access to the Android classes at runtime
            project.withGroovyBuilder {
                getProperty("android").withGroovyBuilder {
                    getProperty("defaultConfig").withGroovyBuilder {
                        setProperty("applicationId", env.skipEnv("PRODUCT_BUNDLE_IDENTIFIER"))
                        setProperty("versionCode", env.skipEnv("CURRENT_PROJECT_VERSION"))
                        setProperty("versionName", env.skipEnv("MARKETING_VERSION"))
                        getProperty("manifestPlaceholders").withGroovyBuilder {
                            // Configures the manifest placeholders for AndroidManifest.xml build-time replacement based on the keys in the Skip.env file
                            // Keys like PRODUCT_BUNDLE_IDENTIFIER and CURRENT_PROJECT_VERSION can be referenced directly in the manifest
                            env.forEach { (key, value) ->
                                setProperty(key.toString(), value)
                            }
                        }
                    }
                }
            }

            // add the "launchDebug" and "launchRelease" commands
            listOf("Debug", "Release").forEach { buildType ->
                task("launch" + buildType) {
                    dependsOn("install" + buildType)
                    doLast {
                        // TODO: if `skip devices` returns more than once value, set ANDROID_SERIAL to one of them or allow customization
                        exec {
                            commandLine = listOf(
                                "adb".withExecutableExtension(),
                                "devices")
                        }

                        exec {
                            commandLine = listOf(
                                "adb".withExecutableExtension(),
                                "shell",
                                "am",
                                "start",
                                "-a",
                                "android.intent.action.MAIN",
                                "-c",
                                "android.intent.category.LAUNCHER",
                                "-n",
                                activity
                            )
                        }
                    }
                }
            }
        }
    }
}


class SkipSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        with(settings) {
            val baseDir = rootDir.resolve("..")
            val env = loadSkipEnv(baseDir.resolve(skipEnvFilename))

            // Use the shared ../.build/Android/ build folder as the gradle build output
            val buildDir = baseDir.resolve(".build")
            val buildOutput = buildDir.resolve("Android")

            gradle.projectsLoaded {
                rootProject.allprojects {
                    layout.buildDirectory.set(buildOutput.resolve(project.name))
                }
            }

            rootProject.name = env.skipEnv("ANDROID_PACKAGE_NAME")
            val swiftProjectName = env.skipEnv("SKIP_PROJECT_NAME")
            val swiftModuleName = env.skipEnv("PRODUCT_NAME")

            // the source for the plugin is linked as part of the SkipUnit transpilation
            val skipOutput = System.getenv("BUILT_PRODUCTS_DIR") ?: System.getProperty("BUILT_PRODUCTS_DIR")

            val outputExt = if (skipOutput != null) ".output" else "" // Xcode saves output in package-name.output; SPM has no suffix
            val skipOutputs: File = if (skipOutput != null) {
                // BUILT_PRODUCTS_DIR is set when building from Xcode, in which case we will re-use Xcode's DerivedData plugin output
                File(skipOutput).resolve("../../../SourcePackages/plugins/")
            } else {
                // SPM output folder is a peer of the parent Package.swift
                buildDir.resolve("plugins/outputs/")
            }
            val projectDir = skipOutputs
                .resolve(swiftProjectName + outputExt)
                .resolve(swiftModuleName)
                .resolve("skipstone")

            // apply the settings directly to get the dependencies, which provides the "libs" versionCatalog
            apply(projectDir.resolve("settings.gradle.kts"))
            // add all the Skip dependant projects
            includeBuild(projectDir)
            // finally, include the local app scaffold
            include(":app")
        }
    }
}

// Look up the expected property in the Skip.env file
private fun Properties.skipEnv(key: String) : String {
    val value = getProperty(key, System.getProperty("SKIP_${key}"))
    if (value == null) {
        throw GradleException("Required key ${key} is not set in top-level ${skipEnvFilename} file or system property SKIP_${key}")
    }
    return value
}

private fun String.withExecutableExtension() : String {
    if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
        return this + ".exe"
    } else {
        return this
    }
}

// Parse the Skip.env key-value pairs, which is expected to be in the parent of the settings.gradle.kts
fun loadSkipEnv(from: File): Properties {
    if (!from.isFile) {
        throw GradleException("Missing expected ${from.name} configuration file in the root folder of the project: ${from}.")
    }

    val props = Properties()
    props.load(FileReader(from, Charset.forName("utf-8")))
    // the Skip.env file is an .xcconfig format, which can use "//" for comments
    // this is interpreted by the Java properties format as a key value pair, so just manually trim it out
    props.remove("//")
    return props
}
