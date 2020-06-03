plugins {
    kotlin("jvm")
    kotlin("kapt")
    java
    id("com.github.johnrengelman.shadow")
    application
}

version = "unspecified"

application {
    mainClassName = "com.dattilio.klient.headless.courses.CoursesAppKt"
}

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.tinder.statemachine:statemachine:0.2.0")
    implementation("io.ktor:ktor-websockets:1.3.1")
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
