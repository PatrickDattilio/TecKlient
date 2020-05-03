plugins {
    kotlin("jvm")
    kotlin("kapt")
    java
    id("com.github.johnrengelman.shadow")
    application
}

version = "unspecified"


application {
    mainClassName = "com.dattilio.klient.plugins.combat.CombatApp"
}

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(project(":api"))
    implementation("javax.inject:javax.inject:1")
    implementation("com.squareup.moshi:moshi:1.9.2")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.2")
    implementation("com.tinder.statemachine:statemachine:0.2.0")
    implementation("io.ktor:ktor-websockets:1.3.1")
    implementation("io.ktor:ktor-client-okhttp:1.3.1")

    testCompileOnly(project(":api"))
    testImplementation("junit", "junit", "4.12")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("com.google.truth:truth:1.0.1")
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

