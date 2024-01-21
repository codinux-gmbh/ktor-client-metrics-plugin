plugins {
	kotlin("jvm") version "1.9.21"
}


repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain(17)
}


val micrometerVersion: String = "1.12.2"

dependencies {
	implementation("io.micrometer:micrometer-core:$micrometerVersion")
	implementation("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")

	implementation("com.squareup.okhttp3:okhttp:4.12.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
}