// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

import org.gradle.api.*
import org.gradle.api.initialization.*
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader
import java.nio.charset.Charset
import java.util.Properties
import java.util.regex.Pattern
import com.android.build.gradle.BaseExtension
import com.android.build.api.dsl.*
import org.gradle.kotlin.dsl.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
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
        val logger: Logger = project.logger
        fun info(message: String) = logger.info(message)
        // output in the standard Gradle warning format, which skip will parse and convert into an Xcode warning
        fun warn(message: String) = logger.warn("w: file://:0:0 ${message}")
        // output in the standard Gradle error format, which skip will parse and convert into an Xcode error
        fun error(message: String) = logger.error("e: file://:0:0 ${message}")

        // TODO: try to add the compose compiler plugin if it does not exist in the project
        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html#migrating-a-compose-multiplatform-project
        // https://android-developers.googleblog.com/2024/04/jetpack-compose-compiler-moving-to-kotlin-repository.html
        if (!project.plugins.hasPlugin("org.jetbrains.kotlin.plugin.compose")) {
            //project.plugins.apply("org.jetbrains.kotlin.plugin.compose")
        }

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
            val applicationId = env.skipEnv("PRODUCT_BUNDLE_IDENTIFIER").replace("-", "_")
            val activity = applicationId + "/" + packageName + ".MainActivity"
            dependencies.add("implementation", appModule)

            //val androidExtension = extensions.getByName("android") as com.android.build.gradle.BaseExtension // Unable to load class 'com.android.build.gradle.BaseExtension'
            // use a Groovy builder, which enables building up the DSL with access to the Android classes at runtime
            project.withGroovyBuilder {
                getProperty("android").withGroovyBuilder {
                    getProperty("defaultConfig").withGroovyBuilder {
                        setProperty("applicationId", applicationId)
                        setProperty("versionCode", env.skipEnv("CURRENT_PROJECT_VERSION").toInt())
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

            data class DeviceInfo(val serialNumber: String, val deviceType: String)

            /// Invoke `adb devices` and parse the output for the list of connected devices
            fun invokeADBDevices() : List<DeviceInfo> {
                val devicesOut = ByteArrayOutputStream()
                exec {
                    commandLine = listOf(
                        "adb".withExecutableExtension(),
                        "devices")
                    standardOutput = devicesOut
                }

                //warn("running adb devices: ${devicesOut}")

                val devicesOutString = devicesOut.toByteArray().toString(Charsets.UTF_8)
                val devices = mutableListOf<DeviceInfo>()

                val androidSerialsEnv = System.getenv("ANDROID_SERIAL")

                devicesOutString.lines().forEach { line ->
                    if (line.endsWith("\tdevice")) {
                        val parts = line.split("\t")
                        if (parts.size == 2) {
                            val serialNumber = parts[0]
                            val deviceType = parts[1]
                            // if we have set the ANDROID_SERIAL environment, only add the
                            // device to the list if it matches the serial number
                            if (androidSerialsEnv == null || androidSerialsEnv.isEmpty() || androidSerialsEnv == serialNumber) {
                                devices.add(DeviceInfo(serialNumber, deviceType))
                            }
                        }
                    }
                }

                return devices
            }

            task("checkDevices") {
                doFirst {
                    val devices = invokeADBDevices()
                    if (devices.size == 0) {
                        error("No connected Android devices or emulators were reported by `adb devices`. To launch the Skip app, start an emulator from the Android Studio Device Manager or use the ~/Library/Android/sdk/emulator/emulator command")
                    }
                }
            }

            // add the "launchDebug" and "launchRelease" commands
            listOf("Debug", "Release").forEach { buildType ->
                task("launch" + buildType) {
                    dependsOn("checkDevices") // check the devices before install to report when there are no devices
                    dependsOn("install" + buildType) // built-in install task

                    doLast {
                        val devices = invokeADBDevices()

                        if (devices.size > 1) {
                            val serials = devices.map({ it.serialNumber }).joinToString(", ")
                            warn("Multiple connected devices were reported by `adb devices`. Will attempt to launch on each device/emulator: ${serials}")
                        }

                        devices.forEach { device ->
                            //warn("launching app ${activity} on device: ${device.serialNumber}")
                            exec {
                                commandLine = listOf(
                                "adb".withExecutableExtension(),
                                "-s",
                                device.serialNumber,
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
}


class SkipSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val logger: Logger = Logging.getLogger(SkipSettingsPlugin::class.java)
        fun info(message: String) = logger.info(message)
        // output in the standard Gradle warning format, which skip will parse and convert into an Xcode warning
        fun warn(message: String) = logger.warn("w: file://:0:0 ${message}")
        // output in the standard Gradle error format, which skip will parse and convert into an Xcode error
        fun error(message: String) = logger.error("e: file://:0:0 ${message}")

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
            val swiftModuleName = env.skipEnv("PRODUCT_NAME")

            // the source for the plugin is linked as part of the SkipUnit transpilation
            val skipOutput = System.getenv("BUILT_PRODUCTS_DIR") ?: System.getProperty("BUILT_PRODUCTS_DIR")
            val skipOutputs: File = if (skipOutput != null) {
                // BUILT_PRODUCTS_DIR is set when building from Xcode, in which case we will use Xcode's DerivedData plugin output folder for the build project
                File(skipOutput).resolve("../../../SourcePackages/plugins/")
            } else {
                // SPM output folder is a peer of the parent Package.swift in the .build folder
                buildDir.resolve("plugins/outputs/")
            }

            //warn("checking skipOutputs: ${skipOutputs}")
            if (!skipOutputs.exists()) {
                error("The expected plugin output folder did not exist at ${skipOutputs}. This may mean that the Skip project was not transpiled successfully, or that the skipstone transpiler plugin is not enabled for the project. Check the gradle log for details and see https://skip.tools/docs/faq/ for troubleshooting.")
            }

            // look in each of the output folders and return the first one for which the ModuleName/skipstone/ folder exists.
            // we used to use the SKIP_PROJECT_NAME project setting, but that requires that the Swift package app name was identical to the folder name in which the project resided, so this is more robust (at the cost of potentially having conflicting module names, if one should exist)
            val projectBaseDir = skipOutputs.listFiles().firstOrNull { path ->
                //warn("checking skipOutputs child: ${path}")
                path.resolve(swiftModuleName).resolve("skipstone").exists()
            }

            if (projectBaseDir == null) {
                error("Could not locate transpiled module for ${swiftModuleName} in ${skipOutputs}. This may mean that the Skip project was not transpiled successfully. Check the gradle log for details and see https://skip.tools/docs/faq/ for troubleshooting.")
            } else {
                val projectDir = projectBaseDir
                    .resolve(swiftModuleName)
                    .resolve("skipstone")

                if (!projectDir.exists()) {
                    error("The folder at ${projectDir} does not exist. This may mean that the Skip project was not transpiled successfully, or the name of the project module is not unique in the packages that were created. Check the gradle log for details and see https://skip.tools/docs/faq/ for troubleshooting.")
                }

                // apply the settings directly to get the dependencies, which provides the "libs" versionCatalog
                apply(projectDir.resolve("settings.gradle.kts"))
                // add all the Skip dependant projects
                includeBuild(projectDir)
                // finally, include the local app scaffold
                include(":app")
            }
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
