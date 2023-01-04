import groovy.lang.Closure
import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.MacConfig
import io.github.fvarrui.javapackager.model.MacStartup
import io.github.fvarrui.javapackager.model.Manifest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.6.7")
    }
}

plugins {
    kotlin("jvm") version "1.8.0"
}

apply(plugin = "io.github.fvarrui.javapackager.plugin")

val kotlinCoroutinesVersion = "1.6.4"
val tornadoFxVersion = "1.7.20"
val log4jVersion = "2.19.0"
val slf4jVersion = "2.0.5"
val kotlinLoggingVersion = "3.0.4"
val testFxVersion = "4.0.16-alpha"
val junitVersion = "5.9.0"
val vlcjVersion = "4.7.2"
val humbleVersion = "0.3.0"
val flywayVersion = "9.10.2"
val controlsFxVersion = "8.40.18"

version = "0.10.1"

val appVersion: String = version as String

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
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
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
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

    implementation("com.github.thomasnield:rxkotlinfx:2.2.2")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")
    implementation("org.nield:rxkotlin-jdbc:0.4.1")
    implementation("de.codecentric.centerdevice:centerdevice-nsmenufx:2.1.7")
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    // Players
    implementation("io.humble:humble-video-all:$humbleVersion")
    implementation("uk.co.caprica:vlcj:$vlcjVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.testfx:testfx-core:$testFxVersion")
    testImplementation("org.testfx:testfx-junit5:$testFxVersion")
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
    isBundleJre = true
    isCustomizedJre = false
    jrePath = File(System.getProperty("java.home"))
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
    dependsOn("jar")
    vmArgs = listOf("-Xms256m", "-Xmx2048m", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC")
}
