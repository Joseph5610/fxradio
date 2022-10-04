import de.dynamicfiles.projects.gradle.plugins.javafx.JavaFXGradlePluginExtension

buildscript {
    dependencies {
        classpath("de.dynamicfiles.projects.gradle.plugins:javafx-gradle-plugin:8.8.2")
    }

    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.4.32"
}

apply(plugin = "javafx-gradle-plugin")

version = "0.9.6"

val kotlinVersion = "1.4.2"
val kotlinCoroutinesVersion = "1.3.4"
val tornadoFxVersion = "1.7.20"
val log4jVersion = "2.18.0"
val slf4jVersion = "1.7.36"
val kotlinLoggingVersion = "1.12.5"
val testFxVersion = "4.0.16-alpha"
val junitVersion = "5.8.2"
val vlcjVersion = "4.7.2"
val humbleVersion = "0.3.0"
val flywayVersion = "8.5.10"
val controlsFxVersion = "8.40.18"

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    dependencies {
        // Align versions of all Kotlin components
        compile(platform("org.jetbrains.kotlin:kotlin-bom"))

        // Use the Kotlin JDK 8 standard library
        compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        compile("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
        compile("org.slf4j:slf4j-api:$slf4jVersion")
        compile("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
        compile("org.apache.logging.log4j:log4j-api:$log4jVersion")
        compile("org.apache.logging.log4j:log4j-core:$log4jVersion")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    compile(project(":api-client"))

    // Local JAR files
    compile(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    compile("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    compile("no.tornado:tornadofx:$tornadoFxVersion")
    compile("org.controlsfx:controlsfx:$controlsFxVersion")
    compile("no.tornado:tornadofx-controlsfx:0.1.1")

    compile("com.github.thomasnield:rxkotlinfx:2.2.2")
    compile("org.xerial:sqlite-jdbc:3.36.0.3")
    compile("org.nield:rxkotlin-jdbc:0.4.1")
    compile("de.codecentric.centerdevice:centerdevice-nsmenufx:2.1.7")
    compile("org.flywaydb:flyway-core:$flywayVersion")

    // Players
    compile("io.humble:humble-video-all:$humbleVersion")
    compile("uk.co.caprica:vlcj:$vlcjVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.testfx:testfx-core:$testFxVersion")
    testImplementation("org.testfx:testfx-junit5:$testFxVersion")
    testImplementation("org.testfx:openjfx-monocle:8u76-b04")
}

configure<JavaFXGradlePluginExtension> {
    val attributes = hashMapOf<String, String>()
    attributes.put("Implementation-Version", version.toString())
    setVerbose(true)
    setAppName("FXRadio")
    // minimal requirement for jfxJar-task
    setManifestAttributes(attributes)
    setMainClass("online.hudacek.fxradio.FxRadioKt")

    // Fix for https://github.com/FibreFoX/javafx-gradle-plugin/issues/146
    setUsePatchedJFXAntLib(false)

    // minimal requirement for jfxNative-task
    setIdentifier("FXRadio")
    setVendor("FXRadio")
    setJfxMainAppJarName("fxRadio.jar")
    setDeployDir("src/main/deploy")
    setNativeOutputDir("build/jfx/native")
    setNativeReleaseVersion(version as String)
    setNeedShortcut(true)
    setBundler("ALL")
    setSkipJNLP(true)

    additionalAppResources = "src/main/deploy/additional"
    var runtimePath = System.getenv("JAVA_HOME")
    if (runtimePath != null && System.getenv("FX_APPEND_PATH") != null) {
        runtimePath = runtimePath + System.getenv("FX_APPEND_PATH")
    }
    logger.info("Runtime path is: $runtimePath")
    bundleArguments = mapOf("licenseType" to "AGPLv3", "licenseFile" to "LICENSE", "runtime" to runtimePath)
}
