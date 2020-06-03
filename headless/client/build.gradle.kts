plugins {
    kotlin("jvm")
    kotlin("kapt")
    java
    id("com.github.johnrengelman.shadow")
    application
}

version = "unspecified"

application {
    mainClassName = "com.dattilio.klient.headless.client.ForwarderKt"
}
repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-websockets:1.3.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}