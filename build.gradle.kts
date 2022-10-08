import io.github.fvarrui.javapackager.model.MacConfig
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
    kotlin("jvm") version "1.7.20"
}

val kotlinVersion = "1.7.20"
val kotlinCoroutinesVersion = "1.6.4"
val tornadoFxVersion = "1.7.20"
val log4jVersion = "2.18.0"
val slf4jVersion = "1.7.36"
val kotlinLoggingVersion = "1.12.5"
val testFxVersion = "4.0.16-alpha"
val junitVersion = "5.9.0"
val vlcjVersion = "4.7.2"
val humbleVersion = "0.3.0"
val flywayVersion = "9.4.0"
val controlsFxVersion = "8.40.18"

version = "0.9.6"

val appVersion: String = version as String

apply(plugin = "io.github.fvarrui.javapackager.plugin")

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
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
        implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
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

    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    implementation("no.tornado:tornadofx:$tornadoFxVersion")
    implementation("org.controlsfx:controlsfx:$controlsFxVersion")
    implementation("no.tornado:tornadofx-controlsfx:0.1.1")

    implementation("com.github.thomasnield:rxkotlinfx:2.2.2")
    implementation("org.xerial:sqlite-jdbc:3.39.3.0")
    implementation("org.nield:rxkotlin-jdbc:0.4.1")
    implementation("de.codecentric.centerdevice:centerdevice-nsmenufx:2.1.7")
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    // Players
    implementation("io.humble:humble-video-all:$humbleVersion")
    implementation("uk.co.caprica:vlcj:$vlcjVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.testfx:testfx-core:$testFxVersion")
    testImplementation("org.testfx:testfx-junit5:$testFxVersion")
    testImplementation("org.testfx:openjfx-monocle:8u76-b04")
}


task<io.github.fvarrui.javapackager.gradle.PackageTask>("jfxNative") {
    mainClass = "online.hudacek.fxradio.FxRadioKt"
    appName = "FXRadio"
    appDescription = "Internet Radio Directory"
    assetsDir = File("src/main/deploy/package")
    outputDirectory = File("build/jfx/native")
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
    manifest(closureOf<io.github.fvarrui.javapackager.model.Manifest> {
        additionalEntries = mapOf(
            "Implementation-Version" to appVersion
        )
    } as groovy.lang.Closure<io.github.fvarrui.javapackager.model.Manifest>)

    macConfig(closureOf<MacConfig> {
        macStartup = io.github.fvarrui.javapackager.model.MacStartup.UNIVERSAL
        isGeneratePkg = false
        isCodesignApp = false
        backgroundImage = File("src/main/deploy/package/mac/background.png")
    } as groovy.lang.Closure<MacConfig>)
    dependsOn("jar")
}