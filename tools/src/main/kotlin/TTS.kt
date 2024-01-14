import utils.readConfig
import java.io.File

fun main() {
    val config = readConfig()["tts"] as Map<String, String>

    val directory = File(config["directory"]!!)
    val voices = enumerateVoices(config["voice"]!!, directory)

    File("input/voice-lines.txt").readLines()
        .map { splitLine(it) }
        .filter { !File("${directory.absolutePath}/out/${it.first}.wav").exists() }
        .forEach { (id, text) ->
            println("Processing $id: '$text'")
            try {
                processLine(id, text, directory, voices)
            } catch (e: Exception) {
                println("Failed to process $id")
            }
        }
}

fun processLine(id: String, text: String, directory: File, voices: List<String>, outPath: String = "./out/$id.wav") {
    val cmd = listOf(
        "tts",
        "--model_name",
        "tts_models/multilingual/multi-dataset/xtts_v2",
        "--language_idx",
        "en",
        "--speaker_wav"
    ) + voices +
            listOf(
                "--out_path",
                outPath,
                "--text",
                text
            )
    println(cmd.joinToString(" "))
    println(directory.runCommand(cmd))
}

private fun splitLine(line: String): Pair<String, String> {
    val i = line.indexOf(",")
    return line.substring(0, i) to line.substring(i + 1, line.length)
}

fun enumerateVoices(prefix: String, directory: File): List<String> {
    return File(directory.absolutePath + "/voices").listFiles()!!.filter { it.name.startsWith(prefix) }.map { "./voices/" + it.name }
}
