plugins {
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(project(":api"))
    implementation("javax.inject:javax.inject:1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.1.1")
    implementation("io.ktor:ktor-websockets:1.3.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.dagger:dagger:2.26")
    kapt("com.google.dagger:dagger-compiler:2.26")
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.6.0")

    implementation( "com.squareup.okhttp3:logging-interceptor:4.4.0")
    implementation("org.fxmisc.richtext:richtextfx:0.10.4")
    implementation("com.tinder.scarlet:scarlet:0.1.10")
    implementation("com.tinder.scarlet:websocket-okhttp:0.1.10")
    implementation("com.tinder.scarlet:message-adapter-moshi:0.1.10")
    implementation("com.tinder.scarlet:stream-adapter-coroutines:0.1.10")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
