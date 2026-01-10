plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.bluewhale"
version = "1.0.7"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.yaml:snakeyaml:2.4")
    implementation("org.springframework.boot:spring-boot:3.4.3")
    
    intellijPlatform {
        create("IC", "2025.1.3")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
            untilBuild = "253.*"
        }
    }
    
    buildSearchableOptions = false
    
    signing {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }
    
    publishing {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
    
    withType<JavaCompile>().configureEach {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}
