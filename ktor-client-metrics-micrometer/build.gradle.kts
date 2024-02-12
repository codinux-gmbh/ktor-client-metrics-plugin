plugins {
    kotlin("jvm")
    id("kotlinx-atomicfu")
}


kotlin {
    jvmToolchain(17)
}


dependencies {
    api(project(":ktor-client-metrics"))

    implementation(libs.ktor.client.core)

    api(libs.micrometer.core)


    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.ktor.client.mock)

    testImplementation(libs.assertk)

    testImplementation(libs.micrometer.registry.prometheus)
}


tasks.test {
    useJUnitPlatform()
}


ext["customArtifactId"] = "ktor-client-metrics-micrometer"

apply(from = "../gradle/scripts/publish-codinux.gradle.kts")