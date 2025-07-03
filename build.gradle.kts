plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3" // IntelliJ 플러그인 최신 버전
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
}

group = "com.bluewhale"
version = "1.0.6"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

intellij {
    version.set("2025.1.3")
    type.set("IC") // IntelliJ Community Edition
    plugins.set(listOf()) // 필요한 플러그인 ID가 있다면 여기에 추가
}

dependencies {
    implementation("org.yaml:snakeyaml:2.4")
    implementation("org.springframework.boot:spring-boot:3.4.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("251")
        untilBuild.set("252.*")
    }

    buildSearchableOptions {
        enabled = false
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    test {
        useJUnitPlatform()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}