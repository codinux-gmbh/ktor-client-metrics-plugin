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
}