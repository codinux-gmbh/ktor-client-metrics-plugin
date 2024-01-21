plugins {
    kotlin("jvm")
    id("kotlinx-atomicfu")
}


kotlin {
    jvmToolchain(17)
}


dependencies {
    implementation(project(":ktor-client-metrics"))

    implementation(libs.ktor.client.core)

    implementation(libs.micrometer.core)


    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.ktor.client.mock)

    testImplementation(libs.assertk)

    testImplementation(libs.micrometer.registry.prometheus)
}


tasks.test {
    useJUnitPlatform()
}