/plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}

kotlin {
    // Enable the default target hierarchy:
    targetHierarchy.default()

    jvm {
        jvmToolchain(11)
        withJava() // not allowed if android { } is present

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()

            testLogging { // This is for logging and can be removed.
                events("passed", "skipped", "failed")
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ktor-client-metrics"
            isStatic = true
        }
    }

    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.coroutines.test)
            implementation(libs.ktor.client.mock)

            implementation(libs.assertk)
        }

        jvmMain {
            dependencies {
                implementation(libs.micrometer.core)
            }
        }
        jvmTest.dependencies {
            implementation(libs.micrometer.registry.prometheus)
        }
    }
}