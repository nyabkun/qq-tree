/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

println("This Gradle script starts with -Dfile.encoding=" + System.getProperty("file.encoding"))

val qMavenArtifactId = "qq-tree"
val qKotlinVersion = "1.8.20"
val qJvmSourceCompatibility = "17"
val qJvmTargetCompatibility = "17"

plugins {
    val kotlinVersion = "1.8.20"
    
    application
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("maven-publish")
    
}

group = "com.nyabkun.qol"

version = "v2023-05-22-bc02"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

java {
    toolchain {
        sourceCompatibility = JavaVersion.toVersion(qJvmSourceCompatibility)
        targetCompatibility = JavaVersion.toVersion(qJvmTargetCompatibility)
    }
}


sourceSets.main {
    java.srcDirs("src-split")

    resources.srcDirs("rsc")
}

sourceSets.test {
    java.srcDirs("src-test-split")

    resources.srcDirs("rsc-test")
}

sourceSets.register("example") {
    java.srcDirs("src-example")

    resources.srcDirs("rsc")

    val jarFile = "$buildDir/libs/$qMavenArtifactId-$version.jar"
    compileClasspath += files(jarFile)
    runtimeClasspath += files(jarFile)
}

tasks.getByName("compileExampleKotlin").dependsOn("jar")

val exampleImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val exampleRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

sourceSets.register("single") {
    java.srcDirs("src-single")
}

val singleImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val singleRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

sourceSets.register("testSingle") {
    java.srcDirs("src-test-single")

    resources.srcDirs("rsc-test")
}

val testSingleImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val testSingleRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
}

tasks {
    jar {
        enabled = true
        
        archiveBaseName.set(qMavenArtifactId)

        from(sourceSets.main.get().output)

        manifest {
            attributes(
                    "Implementation-Title" to qMavenArtifactId,
                    "Implementation-Version" to project.version
            )
        }
    }
    
    val qSrcJar by registering(Jar::class) {
        archiveBaseName.set(qMavenArtifactId)
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    artifacts {
        archives(qSrcJar)
        archives(jar)
    }
    
    distZip {
        enabled = false
    }
    
    distTar {
        enabled = false
    }
    
    startScripts {
        enabled = false
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    withType<JavaExec>().configureEach {
        jvmArgs("-Dfile.encoding=UTF-8")
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = JavaVersion.toVersion(qJvmTargetCompatibility).toString()
    }

    register<JavaExec>("qRunTestSplit") {
        mainClass.set("nyab.util.QTreeNodeTestKt")
        classpath = sourceSets.get("test").runtimeClasspath
    }

    register<JavaExec>("qRunTestSingle") {
        mainClass.set("QTreeNodeTestKt")
        classpath = sourceSets.get("testSingle").runtimeClasspath
    }

    register<JavaExec>("qRunExample") {
        mainClass.set("tree.QTreeNodeExampleKt")
        classpath = sourceSets.get("example").runtimeClasspath
    }
    
    getByName<Test>("test") {
        dependsOn("qRunTestSingle")
        dependsOn("qRunTestSplit")
        dependsOn("qRunExample")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = qMavenArtifactId
           
            from(components["kotlin"])

            artifact(tasks.getByName("qSrcJar")) {
                classifier = "sources"
            }
        }
    }
//      // GitHub Packages
//      repositories {
//          maven {
//              url = uri("https://maven.pkg.github.com/nyabkun/qq-tree")
//              credentials {
//                  username = "nyabkun"
//                  password = File("../../.q_gpr.key").readText(Charsets.UTF_8).trim()
//              }
//          }
//      }
}