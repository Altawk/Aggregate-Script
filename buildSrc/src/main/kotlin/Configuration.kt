// Group
const val rootGroup = "cn.altawk.asl"
const val githubGroup = "io.github.altawk.asl"

// Gradle Plugin Version
const val kotlinVersion = "1.9.22"
const val shadowJarVersion = "8.1.1"
const val nexusPublishVersion = "1.3.0"

// Taboolib Version
val taboolibVersion = GithubAPI.getLatestRelease("TabooLib", "taboolib", "6.1.0")
    .also { println("Using taboolib-version = $it") }

// Default Github Repo
val defaulGithubRepo = "Altawk/Aggregate-Script"