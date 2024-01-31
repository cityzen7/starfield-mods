import utils.readConfig
import java.io.File

fun main() {
    val config = readConfig()
    val ttsConfig = config["tts"] as Map<String, String>
    val coquiDir = File(ttsConfig["directory"]!!)
    val lines = File("input/sayMyName/cleaner.txt").readLines().filter { it.startsWith("Failed to process") }

    lines.forEach { line ->
        val (name, id) = line.replace("Failed to process ", "").let { it.substring(0, it.indexOf(":")) }.split(" ")
        val file = File(coquiDir.absolutePath + "/out/$name/$id.wav")
        println(file.absolutePath)
        if (file.exists()) file.delete()
    }
}
