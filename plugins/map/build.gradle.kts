plugins {
    kotlin("jvm")
    kotlin("kapt")
}

version = "unspecified"

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(project(":api"))
    implementation("com.tinder.statemachine:statemachine:0.2.0")
    implementation("javax.inject:javax.inject:1")
    implementation("com.squareup.moshi:moshi:1.9.2")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.1.1")
    implementation("io.ktor:ktor-websockets:1.3.1")
    implementation("io.ktor:ktor-client-okhttp:1.3.1")
    implementation("guru.nidi:graphviz-java:0.15.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}