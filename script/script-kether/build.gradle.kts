import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import groovy.util.Node

val scriptName = "Script-Kether"
group = githubGroup
version = taboolibVersion

// 判断版本是否发布过, 用于防止重复发布
val isPublished = (getLatestPackage(githubOwner, "$group.$name") == version)
    .also { if(it) println("[$scriptName] This version has published, skip publish.") }

dependencies {
    // Default
    compileOnly("io.izzel.taboolib:common-env:$taboolibVersion")
    compileOnly("io.izzel.taboolib:common-util:$taboolibVersion")
    compileOnly("io.izzel.taboolib:common:$taboolibVersion")
    compileOnly("io.izzel.taboolib:module-lang:$taboolibVersion")
    // Kether - Main
    implementation("io.izzel.taboolib:module-kether:$taboolibVersion")
    // Kether - JavaScript
    implementation("io.izzel.taboolib:expansion-javascript:$taboolibVersion")
}

tasks {
    withType<ShadowJar> {
        archiveAppendix.set("")
        archiveClassifier.set("")
        /**
         * 删除关于Bukkit的内容
         */
        exclude("taboolib/module/kether/action/game/**")
    }
    build { dependsOn(shadowJar) }
    // 禁用模块元数据 (防止原taboolib模块被依赖)
    withType<GenerateModuleMetadata> { enabled = false }
}

// 发布
publishing {
    repositories {
        mavenLocal()
        maven("https://maven.pkg.github.com/$githubRepo") {
            credentials { username = githubUser; password = githubToken }
        }
    }
    publications {
        create<MavenPublication>("mavenKether") {
            from(components["java"])
            pom {
                name = scriptName
                description = "A Script Language from Taboolib"
                applyDefaults(githubRepo)
                // 直接从pom中删除依赖——简单粗暴
                withXml {
                    asNode().children().removeIf {
                        (it as Node).name().toString().endsWith("dependencies")
                    }
                }
            }
        }
    }
}

// 签名 (必须)
signing {
    sign(publishing.publications)
}

// 不重复发布
tasks.withType<AbstractPublishToMaven> { onlyIf { !isPublished } }