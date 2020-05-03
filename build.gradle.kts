
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    application
    java
    kotlin("jvm") version "1.3.61"
    kotlin("kapt") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    mavenCentral()
    jcenter()
}

application {
    mainClassName = "com.dattilio.klient.App"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":client"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
