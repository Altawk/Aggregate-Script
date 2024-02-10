rootProject.name = "Aggregate-Script"

applyAll("script")

fun Settings.applyAll(name: String) {
    File(rootDir, name).listFiles()?.filter { it.isDirectory }?.forEach {
        include("$name:${it.name}")
    }
}