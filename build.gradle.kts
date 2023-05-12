import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("kapt") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    idea
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint").version("10.3.0")
    id("io.gitlab.arturbosch.detekt").version("1.21.0")
    id("org.owasp.dependencycheck").version("7.1.1")
}

group = "dev.krafczyk"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.tukaani:xz:1.9")
    implementation("io.github.microutils:kotlin-logging:2.1.23")
    implementation("com.pivovarit:parallel-collectors:2.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.amshove.kluent:kluent:1.68")
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
}

pluginManager.withPlugin("idea") {
    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    enableExperimentalRules.set(false)
    filter {
        exclude { element -> element.file.path.contains("generated/") }
    }
}

configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    config = files("${project.rootDir}/detekt.yml")
    buildUponDefaultConfig = true
}

configure<org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension> {
    failBuildOnCVSS = 0f
    suppressionFile = "${project.rootDir}/owasp-suppression.xml"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
