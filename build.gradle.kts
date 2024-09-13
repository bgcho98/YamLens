plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
    id("org.jetbrains.kotlin.jvm") version "1.9.0" // Kotlin 플러그인 추가
}

group = "com.bluewhale"
version = "1.0.4"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

intellij {
    version.set("2024.2.1")
    type.set("IC")
    plugins.set(listOf())
}

dependencies {
    implementation("org.yaml:snakeyaml:1.33")
    implementation("org.springframework.boot:spring-boot:2.7.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0") // Kotlin 표준 라이브러리 추가
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("252.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    named<Test>("test") {
        useJUnitPlatform()
    }
}

