import utils.readConfig
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val doSingleGroup = true

fun main() {
    val config = readConfig()
    val sayMyNameConfig = config["sayMyName"] as Map<String, Any>
    val characters = sayMyNameConfig["characters"] as Map<String, String>
    val convertedDir = File(sayMyNameConfig["convertedOut"] as String)
    val publishDir = File(sayMyNameConfig["stagingDirectory"] as String + "../publish")
    val playerNameGroups = File("input/sayMyName/name-groups.txt").readLines()
        .map { it.split(",") }.map { group -> group.map { it.trim().capitalize() } }
        .let { if (doSingleGroup) it.take(1) else it }

    characters.forEach { (characterName, npcFolder) ->
        playerNameGroups.forEach { group ->
            val groupName = "say-my-name-$characterName-"+ group.first() + "-" + group.last()
            group.forEach { player ->
                val publishFolder = File(publishDir.absolutePath + "/$characterName/$groupName/Data/sound/voice/starfield.esm/$npcFolder/$player").also { it.mkdirs() }
                val playerFolder = File(convertedDir.absolutePath + "/$characterName/$player")
                playerFolder.stripWwiseSuffix()
                playerFolder.listFiles()?.filter { it.name.endsWith(".wem") }?.forEach {
                    Files.copy(it.toPath(), File(publishFolder.absolutePath + "/" + it.name).toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
    }
}