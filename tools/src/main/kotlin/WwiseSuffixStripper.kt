import utils.readConfig
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

private const val dryRun = false


fun main() {
    val config = readConfig()["starfield"] as Map<String, String>

    File(config["suffixStripper"]!!).stripWwiseSuffix()

}

fun File.stripWwiseSuffix() {
    listFiles()?.filter { it.extension == "wem" }?.forEach { file ->
        var newName = file.nameWithoutExtension

        val underscoreStop = file.name.indexOf("_")
        if (underscoreStop != -1) {
            newName = newName.substring(0, underscoreStop)
        }
        val dashStop = file.name.indexOf("-")
        if (dashStop != -1) {
            newName = newName.substring(dashStop + 1)
        }

        if (newName != file.nameWithoutExtension) {
            val newPath = file.parent + "/" + newName + ".wem"
            if (dryRun) {
                println("Rename ${file.name} to $newPath")
            } else {
                File(newPath).let { if (it.exists()) it.delete() }
                Files.move(file.toPath(), Path(newPath))
            }
        }
    }
}