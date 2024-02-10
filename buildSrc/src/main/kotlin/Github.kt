import org.gradle.api.Project
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

val Project.githubRepo: String
    get() = try {
        this.getProperty("githubRepo") ?: System.getenv("GITHUB_REPO")
    } catch (ex: NullPointerException) {
        defaulGithubRepo
    }

val Project.githubToken: String
    get() = (this.getProperty("githubKey", "githubToken") ?: System.getenv("GITHUB_TOKEN")).toString()

val Project.githubUser
    get() = this.getProperty("githubUsername", "githubUser") ?: try {
        System.getenv("GITHUB_USERNAME")
    } catch (ex: NullPointerException) {
        System.getenv("GITHUB_ACTOR")
    }.toString()

val Project.githubOwner get() = this.githubRepo.split('/')[0]

val Project.githubRepoName get() = this.githubRepo.split('/')[1]

object GithubAPI {

    fun getLatestRelease(
        repoOwner: String,
        repoName: String,
        overwrite: String? = null,
        fallback: String? = null,
    ): String = overwrite ?: try {
        // Connect
        val url = APIGetter.releaseUrl(repoOwner, repoName)
        val connection = prepareConnect(url)
        // Read
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()
        val sign = "\"tag_name\":"
        val index = response.indexOf(sign) + sign.length + 1
        val tagName = response.substring(index, response.indexOf(",", index))
        tagName.replace("\"", "")
    } catch (e: Exception) {
        e.printStackTrace()
        fallback ?: throw e
    }

    fun getLatestPackage(user: String, name: String, token: String, type: String = "maven"): String {
        // Connect
        val url = APIGetter.packageUrlO(user, name, type)
        val connection = prepareConnect(url, token)
        // Read
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()
        val sign = "\"name\":"
        val index = response.indexOf(sign) + sign.length + 1
        val packageName = response.substring(index, response.indexOf(",", index))
        return packageName.replace("\"", "")
    }

    fun prepareConnect(url: URL) = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        setRequestProperty("Accept", "application/vnd.github+json")
    }

    fun prepareConnect(url: URL, token: String) = prepareConnect(url).apply {
        setRequestProperty("Authorization", "Token $token")
    }

    object APIGetter {

        fun releaseUrl(owner: String, name: String) =
            URL("https://api.github.com/repos/$owner/$name/releases/latest")

        fun packageUrlU(user: String, name: String, type: String) =
            URL("https://api.github.com/users/$user/packages/$type/$name/versions")

        fun packageUrlO(org: String, name: String, type: String) =
            URL("https://api.github.com/orgs/$org/packages/$type/$name/versions")

    }

}

fun Project.getLatestPackage(user: String, name: String, type: String = "maven"): String? =
    kotlin.runCatching { GithubAPI.getLatestPackage(user, name, githubToken, type) }.getOrNull()