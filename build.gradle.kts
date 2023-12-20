plugins {
    id("java")
    application
}

group = "org.example"
version = "0.0.1"

val iMainClass = "com.badflandre.search.util.TestExtractor"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.htmlparser:htmlparser:2.1")
    implementation("org.apache.lucene:lucene-core:2.0.0")
    implementation("org.jeasy:easy-random-core:5.0.0")

    implementation("io.vertx:vertx-core:4.4.0")
    implementation("io.vertx:vertx-web:4.4.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.3.11")
    implementation("info.picocli:picocli:4.7.5")

    implementation(fileTree("libs") {
        includes.add("*.jar")
    })

}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set(iMainClass)
    applicationDefaultJvmArgs = listOf(
        "-Xmx10G",
        "-Xms2G",
    )
}

tasks.jar {

    manifest {
        attributes("Main-Class" to "org.example.Main")
    }

    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
