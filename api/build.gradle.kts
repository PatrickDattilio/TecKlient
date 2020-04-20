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
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
//    implementation("org.pf4j:pf4j:3.2.0")
    implementation("io.ktor:ktor-websockets:1.3.1")
    implementation("io.ktor:ktor-client-okhttp:1.3.1")
    implementation("com.google.dagger:dagger:2.26")
    kapt("com.google.dagger:dagger-compiler:2.26")
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
