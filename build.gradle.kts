buildscript {
    val kotlinVersion: String by extra
    val atomicFUVersion: String by extra

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:$atomicFUVersion")
    }
}




allprojects {
    group = "net.codinux.web"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/codinux/ktor-client-metrics-plugin"

    ext["projectDescription"] = "Publishes metrics for Ktor client"
}


tasks.register("publishAllToMavenLocal") {
    dependsOn(
        ":ktor-client-metrics:publishToMavenLocal",
        ":ktor-client-metrics-micrometer:publishToMavenLocal"
    )
}

tasks.register("publishAll") {
    dependsOn(
        ":ktor-client-metrics:publish",
        ":ktor-client-metrics-micrometer:publish"
    )
}