val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val sqldelight_version: String by project
val koin_version: String by project
val koin_ktor_version: String by project
val aws_sdk_version: String by project

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.4")
    }
}

plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.4.21"
    id("io.ktor.plugin") version "2.2.1"
    id("com.squareup.sqldelight") version "1.5.4"
}

group = "org.ppfc"
version = "0.1.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-network-tls-certificates:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.insert-koin:koin-ktor:$koin_ktor_version")
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("com.squareup.sqldelight:sqlite-driver:$sqldelight_version")
    implementation("com.squareup.sqldelight:coroutines-extensions:$sqldelight_version")
    implementation("org.xerial:sqlite-jdbc:3.39.3.0")
    implementation("aws.sdk.kotlin:cognitoidentityprovider:$aws_sdk_version")
    implementation("aws.sdk.kotlin:workspaces:$aws_sdk_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

ktor {
    fatJar {
        archiveFileName.set("api.jar")
    }
}

tasks {
    create<Delete>("cleanLibs") {
        delete("build/libs")
    }

    register<Copy>("copyResourcesToDistribution") {
        dependsOn(":shadowJar")
        from("resources")
        into("build/libs/resources")
    }

    register("createRunBatFile") {
        val outputFile = file("/build/libs/run.bat")
        outputs.file(outputFile)
        doLast {
            outputFile.appendText("""
                java -jar api.jar
                pause
            """.trimIndent())
        }
    }

    register("createProcfile") {
        val outputFile = file("/build/libs/Procfile")
        outputs.file(outputFile)
        doLast {
            outputFile.appendText("""
                web: java -Xms256m -jar api.jar
            """.trimIndent())
        }
    }

    create<Zip>("createDistributable") {
        dependsOn(":cleanLibs")
        dependsOn(":copyResourcesToDistribution")
        dependsOn(":createRunBatFile")
        dependsOn(":createProcfile")
        from("build/libs")
    }

    register("deploy") {
        dependsOn(":createDistributable")
    }
}

sqldelight {
    database("Database") {
        packageName = "org.ppfc.api.database"
    }
}