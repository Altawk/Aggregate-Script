import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom

val Project.nexusUser
    get() = (this.getProperty("nexusUsername", "nexusName", "nexusUser") ?: System.getenv("NEXUS_USERNAME")).toString()

val Project.nexusPassword get() = (this.getProperty("nexusPassword") ?: System.getenv("NEXUS_PASSWORD")).toString()

fun MavenPom.applyDefaults(repo: String) {
    url.set("https://github.com/$repo")
    licenses {
        license {
            name.set("The MIT License")
            url.set("https://opensource.org/license/mit/")
        }
    }
    developers {
        developer {
            id.set("TheFloodDragon")
            name.set("Dragon Flood")
            email.set("1610105206@qq.com")
        }
    }
    scm {
        connection.set("scm:git:git://github.com/$repo.git")
        developerConnection.set("scm:git:git@github.com:$repo.git")
        url.set("https://github.com/$repo")
    }
}

fun Project.getProperty(vararg names: String): String? {
    names.forEach { name ->
        this.findProperty(name)?.let { return it.toString() }
    }; return null
}