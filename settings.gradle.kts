plugins {
    id("com.gradleup.nmcp.settings") version("1.4.4")
}

rootProject.name = "logger-log4j2"

nmcpSettings {
    centralPortal {
        username = System.getenv("sonatype.username")
        password = System.getenv("sonatype.password")
        publishingType = "AUTOMATIC"
    }
}
