import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.mingliqiye.logger"
version = "1.0.6"

dependencies {
    api("org.slf4j:slf4j-api:2.0.17")
    api("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.1")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.2")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.javadoc.configure {
    options.encoding = "UTF-8"
}
tasks.javadoc{
    enabled = false
}

kotlin {
    jvmToolchain(8)
}


tasks.withType<org.gradle.jvm.tasks.Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("LICENSE") { into("META-INF") }
    from("NOTICE") { into("META-INF") }
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.mingliqiye.utils.main.Main",
                "Specification-Title" to rootProject.name,
                "Specification-Version" to rootProject.version,
                "Specification-Vendor" to "minglipro",
                "Specification-Build-Time" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS")),
                "Specification-Package" to rootProject.group,
                "Specification-Build-Number" to "1",
                "Specification-Build-OS" to System.getProperty("os.name"),
                "Specification-Build-Java" to System.getProperty("java.version"),
                "Specification-Build-Java-Vendor" to System.getProperty("java.vendor"),
                "Specification-Build-Java-Vendor-URL" to System.getProperty("java.vendor.url"),
                "Implementation-Title" to rootProject.name,
                "Implementation-Version" to rootProject.version,
                "Implementation-Package" to rootProject.group,
                "Implementation-Vendor" to "minglipro",
                "Implementation-Build-Time" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS"))

            )
        )
    }
}

tasks.register<Jar>("javaDocJar") {
    group = "build"
    archiveClassifier.set("javadoc")
    dependsOn("dokkaJavadoc")
    from(buildDir.resolve("dokka/javadoc"))
}
tasks.register<Jar>("kotlinDocJar") {
    group = "build"
    archiveClassifier.set("kotlindoc")
    dependsOn("dokkaHtml")
    from(buildDir.resolve("dokka/html"))
}
tasks.build {
    dependsOn("javaDocJar", "kotlinDocJar")
}


publishing {
    repositories {
        maven {
            name = "MavenRepositoryRaw"
            url = uri("C:/data/git/maven-repository-raw")
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifacts.clear()
            artifact(tasks.named("jar"))
            artifact(tasks.named("sourcesJar"))
            artifact(tasks.named("javaDocJar"))
            artifact(tasks.named("kotlinDocJar"))
            pom {
                name = "logger-log4j2"
                url = "https://mingliqiye.com"
                description = "A Java/kotlin Logger"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "minglipro"
                        name = "mingli"
                        email = "minglipro@163.com"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/minglipro/logger-log4j2.git"
                    developerConnection = "scm:git:https://git.mingliqiye.com:minglipro/logger-log4j2.git"
                    url = "https://github.com/minglipro/logger-log4j2"
                }
            }
        }
    }
    signing {
        sign(
            publishing.publications
        )
    }
}

tasks.named("generateMetadataFileForMavenJavaPublication") {
    dependsOn("javaDocJar", "kotlinDocJar")
}

signing {
    val secretKeyRingFile = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    val keyId = System.getenv("SIGNING_KEY_ID")
    project.ext.set("signing.secretKeyRingFile", secretKeyRingFile)
    project.ext.set("signing.keyId", keyId)
    project.ext.set("signing.password", "")
}
