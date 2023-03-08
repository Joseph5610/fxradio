import groovy.lang.Closure
import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.MacConfig
import io.github.fvarrui.javapackager.model.MacStartup
import io.github.fvarrui.javapackager.model.Manifest
import io.github.fvarrui.javapackager.model.WindowsConfig

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.7.0")
    }
}

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("application")
}

apply(plugin = "io.github.fvarrui.javapackager.plugin")

val kotlinCoroutinesVersion = "1.6.4"
val tornadoFxVersion = "2.0.0-SNAPSHOT"
val log4jVersion = "2.19.0"
val slf4jVersion = "2.0.5"
val kotlinLoggingVersion = "3.0.4"
val testFxVersion = "4.0.16-alpha"
val junitVersion = "5.9.0"
val vlcjVersion = "4.8.2"
val humbleVersion = "0.3.0"
val flywayVersion = "9.10.2"
val controlsFxVersion = "11.1.2"

version = "0.13.0"

val appVersion: String = version as String

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        // Align versions of all Kotlin components
        implementation(platform(kotlin("bom")))

        // Use the Kotlin JDK 8 standard library.
        implementation(kotlin("stdlib-jdk8"))

        implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$kotlinCoroutinesVersion")
    }

    kotlin {
        jvmToolchain(17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":api-client"))

    // Local JAR files
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("no.tornado:tornadofx:$tornadoFxVersion")
    implementation("org.controlsfx:controlsfx:$controlsFxVersion")
    implementation("no.tornado:tornadofx-controlsfx:0.1.1")

    implementation("org.pdfsam.rxjava3:rxjavafx:3.0.2")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")
    implementation("de.jangassen:nsmenufx:3.1.0")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("com.github.davidmoten:rxjava3-jdbc:0.1.3")

    // Players
    implementation("io.humble:humble-video-all:$humbleVersion")
    implementation("uk.co.caprica:vlcj:$vlcjVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.testfx:testfx-core:$testFxVersion")
    testImplementation("org.testfx:testfx-junit5:$testFxVersion")
}

javafx {
    version = "19.0.2.1"
    modules = mutableListOf("javafx.controls", "javafx.fxml", "javafx.media", "javafx.swing", "javafx.web")
}

application {
    mainClass.set("online.hudacek.fxradio.FxRadioKt")
    applicationDefaultJvmArgs = listOf(
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
    )
}

task<PackageTask>("jfxNative") {
    mainClass = "online.hudacek.fxradio.FxRadioKt"
    appName = "FXRadio"
    appDescription = "Internet Radio Directory"
    assetsDir = File("${project.rootDir}/src/main/deploy/package")
    outputDirectory = File("${project.buildDir}/jfx/native")
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
        isDisableWelcomePage = false
        isDisableFinishedPage = false
        isDisableRunAfterInstall = false
    } as Closure<WindowsConfig>)
    dependsOn("jar")
    vmArgs = listOf("-Xms256m",
        "-Xmx2048m",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseG1GC"
    )
}
