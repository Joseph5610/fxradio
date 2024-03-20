import groovy.lang.Closure
import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.MacConfig
import io.github.fvarrui.javapackager.model.MacStartup
import io.github.fvarrui.javapackager.model.SetupMode
import io.github.fvarrui.javapackager.model.WindowsConfig
import io.github.fvarrui.javapackager.model.Manifest
import org.gradle.internal.os.OperatingSystem

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.7.2")
    }
}

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.openjfx.javafxplugin") version "0.0.14"
    id("application")
}

apply(plugin = "io.github.fvarrui.javapackager.plugin")

val kotlinCoroutinesVersion = "1.8.0"
val tornadoFxVersion = "2.0.0-SNAPSHOT"
val log4jVersion = "2.23.1"
val slf4jVersion = "2.0.12"
val kotlinLoggingVersion = "3.0.5"
val testFxVersion = "4.0.16-alpha"
val junitVersion = "5.10.2"
val vlcjVersion = "4.8.2"
val humbleVersion = "0.3.0"
val flywayVersion = "10.10.0"
val controlsFxVersion = "11.2.0"

val defaultAppJvmArgs = listOf(
    // Tornadofx
    "--add-opens=javafx.controls/javafx.scene.control.skin=ALL-UNNAMED",
    "--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED",
    "--add-opens=javafx.controls/javafx.scene.control=ALL-UNNAMED",
    // necessary for ControlsFX
    "--add-opens=javafx.base/com.sun.javafx.event=ALL-UNNAMED",
    "--add-opens=javafx.base/com.sun.javafx.collections=ALL-UNNAMED",
    "--add-opens=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED",
    "--add-opens=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
    "--add-opens=javafx.graphics/com.sun.javafx.scene.traversal=ALL-UNNAMED",
    "--add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
)

version = "0.19.0"

val appVersion: String = version as String

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        // Align versions of all Kotlin components
        implementation(platform(kotlin("bom")))

        // Use the Kotlin JDK 8 standard library.
        implementation(kotlin("stdlib"))

        implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
            vendor.set(JvmVendorSpec.ADOPTIUM)
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = defaultAppJvmArgs
}

dependencies {
    implementation(project(":api-client"))

    implementation("no.tornado:tornadofx:$tornadoFxVersion")
    implementation("org.controlsfx:controlsfx:$controlsFxVersion")
    implementation("no.tornado:tornadofx-controlsfx:0.1.1")

    implementation("org.pdfsam.rxjava3:rxjavafx:3.0.3")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("de.jangassen:nsmenufx:3.1.0")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("com.github.davidmoten:rxjava3-jdbc:0.1.4") {
        exclude("com.google.code.findbugs", "jsr305")
        exclude("com.google.code.findbugs", "annotations")
        exclude("net.jcip", "jcip-annotations")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$kotlinCoroutinesVersion")

    // Players
    val os = OperatingSystem.current()
    implementation("io.humble:humble-video-noarch:$humbleVersion")
    if (os.isMacOsX) {
        implementation("io.humble:humble-video-arch-x86_64-apple-darwin18:$humbleVersion")
    } else if (os.isWindows) {
        implementation("io.humble:humble-video-arch-x86_64-w64-mingw32:$humbleVersion")
        implementation("io.humble:humble-video-arch-i686-w64-mingw32:$humbleVersion")
    } else if (os.isLinux) {
        implementation("io.humble:humble-video-arch-i686-pc-linux-gnu6:$humbleVersion")
        implementation("io.humble:humble-video-arch-x86_64-pc-linux-gnu6:$humbleVersion")
    }
    implementation("uk.co.caprica:vlcj:$vlcjVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.testfx:testfx-core:$testFxVersion")
    testImplementation("org.testfx:testfx-junit5:$testFxVersion")
}

configurations {
    all {
        exclude(group = "net.java.dev.jna", module = "jna")
        exclude(group = "net.java.dev.jna", module = "jna-platform")
        exclude(group = "org.openjfx", module = "javafx-web")
        exclude(group = "org.openjfx", module = "javafx-swing")
        exclude(group = "org.openjfx", module = "javafx-fxml")
    }
}

javafx {
    version = "21.0.2"
    modules = mutableListOf("javafx.controls", "javafx.media")
}

application {
    mainClass.set("online.hudacek.fxradio.FxRadioKt")
    applicationDefaultJvmArgs = defaultAppJvmArgs
}

task<PackageTask>("jfxNative") {
    val outputDir = project.layout.buildDirectory.dir("jfx/native")
    mainClass = "online.hudacek.fxradio.FxRadioKt"
    appName = "FXRadio"
    appDescription = "Internet Radio Directory"
    assetsDir = File("${project.rootDir}/src/main/deploy/package")
    outputDirectory = outputDir.get().asFile
    displayName = "FXRadio"
    version = appVersion
    url = "https://hudacek.online/fxradio"
    isCustomizedJre = false
    organizationName = "FXRadio"
    organizationUrl = "https://hudacek.online/fxradio"
    organizationEmail = "fxradio@hudacek.online"
    isCreateZipball = true
    manifest(closureOf<Manifest> {
        additionalEntries = mapOf(
            "Implementation-Version" to appVersion
        )
    } as Closure<Manifest>)

    macConfig(closureOf<MacConfig> {
        macStartup = MacStartup.UNIVERSAL
        isGeneratePkg = false
        isCodesignApp = false
        backgroundImage = File("src/main/deploy/package/mac/background.png")
    } as Closure<MacConfig>)
    winConfig(closureOf<WindowsConfig> {
        isGenerateSetup = false
        isGenerateMsi = true
        setupMode = SetupMode.askTheUser
        productVersion = appVersion
        fileVersion = appVersion
        isDisableDirPage = false
        isDisableProgramGroupPage = false
        isDisableWelcomePage = false
        isDisableFinishedPage = false
        isDisableRunAfterInstall = false
        isRemoveOldLibs = true
    } as Closure<WindowsConfig>)
    dependsOn("jar")
    vmArgs = listOf(
        "-Xms256m",
        "-Xmx1500m",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseG1GC"
    )
}
