import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("com.github.johnrengelman.shadow") version shadowJarVersion apply false
    id("io.github.gradle-nexus.publish-plugin") version nexusPublishVersion
}

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    repositories {
        mavenCentral()
        maven("http://ptms.ink:8081/repository/releases") { isAllowInsecureProtocol = true }
    }

    dependencies {
        // Kotlin 标准库
        compileOnly(kotlin("stdlib"))
    }

    tasks {
        // 编码设置
        withType<JavaCompile> { options.encoding = "UTF-8" }
        // Kotlin Jvm 设置
        withType<KotlinCompile> {
            kotlinOptions { jvmTarget = "1.8" }
        }
    }

    // Java 版本设置
    java {
        withSourcesJar()
        withJavadocJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

group = githubGroup

// Nexus Settings
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl = uri("https://s01.oss.sonatype.org/service/local/")
            snapshotRepositoryUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            username = nexusUser
            password = nexusPassword
        }
    }
}
