import utils.combineAudio
import utils.getSilences
import utils.readConfig
import utils.trimAudio
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

const val onlyOneName = true
const val exitOnError = false
const val skipExisting = true
val onlyLine: String = ""

data class Line(val id: String, val start: Double, val end: Double, val content: String)

fun main() {
    val config = readConfig()
    val ttsConfig = config["tts"] as Map<String, String>
    val sayMyNameConfig = config["sayMyName"] as Map<String, Any>
    val coquiDir = File(ttsConfig["directory"]!!)
    val stagingDir = File(sayMyNameConfig["stagingDirectory"] as String)
    val characters = (sayMyNameConfig["characters"] as Map<String, String>).keys
    val playerNames = File("input/sayMyName/names.txt").readLines()
        .flatMap { it.split(",") }.map { it.trim().capitalize() }
        .let { if (onlyOneName) it.take(1) else it }

    characters.forEach { character ->
        println("Processing $character")
        val voices = enumerateVoices(character, coquiDir)
        val workingDir = File(stagingDir.absolutePath + "/$character")
        val tempFile = File(workingDir.absolutePath + "/temp.wav")
        val tempFile2 = File(workingDir.absolutePath + "/temp1.wav")
        val tempFile3 = File(workingDir.absolutePath + "/temp2.wav")
        val rawLines = File("input/sayMyName/$character.txt").readLines()

        playerNames.forEach { playerName ->
            File(workingDir.absolutePath + "/out/$playerName/").mkdirs()
            File(coquiDir.absolutePath + "/out/$playerName/").mkdirs()
            rawLines.map { it.toLine(playerName) }.let { if (onlyLine.isNotEmpty()) it.filter { line -> line.id == onlyLine } else it }
                .filter { !skipExisting || !File("${workingDir.absolutePath}/out/$playerName/${it.id}.wav").exists() }
                .map { line ->
                    try {
                        val ttsOut = File(coquiDir.absolutePath + "/out/$playerName/${line.id}.wav")
                        if (!ttsOut.exists()) {
                            println("Generating ${line.id}: '${line.content}'")
                            processLine(line.id, line.content, coquiDir, voices, "./out/$playerName/${line.id}.wav")
                        }
                    } catch (e: Exception) {
                        println(red("Failed to generate $playerName ${line.id}: ${e.message}"))
                    }
                    line
                }
                .forEach { line ->
                    println("Processing ${line.id}: '${line.content}'")
                    try {
                        val ttsOut = File(coquiDir.absolutePath + "/out/$playerName/${line.id}.wav")
                        Files.copy(ttsOut.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

                        val silences = getSilences(tempFile)

                        //Start: next end of silence after the start timestamp
                        val nameStart = silences.filter { it.end > line.start }.minByOrNull { it.end }?.end ?: throw IllegalStateException("Could not find a silence before the name")
                        //End: latest start of silence BEFORE the end timestamp
                        val nameEnd = silences.filter { it.start < line.end }.maxByOrNull { it.start }?.start ?: throw IllegalStateException("Could not find a silence after the name")

                        cleanName(tempFile, tempFile2, nameStart, nameEnd)
                        combineFile(line.id, tempFile2, tempFile3)
                        val finished = File(workingDir.absolutePath + "/out/$playerName/${line.id}.wav")
                        Files.copy(tempFile3.toPath(), finished.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: Exception) {
                        println(red("Failed to process $playerName ${line.id}: ${e.message ?: e.stackTraceToString()}"))
                        if (exitOnError) throw IllegalStateException("Encountered Error")
                    }
                    tempFile2.delete()
                    tempFile3.delete()
                }
        }
//        tempFile.delete()
    }
}

private fun String.toLine(name: String): Line {
    val parts = split(",")

    return when {
        parts.getOrNull(2)?.toDoubleOrNull() != null -> {
            val contentStart = (parts[0] + parts[1] + parts[2]).length + 3
            val content = substring(contentStart).replace("{name}", name)
            Line(parts[0], parts[1].toDouble(), parts[2].toDouble(), content)
        }

        parts[1].toDoubleOrNull() != null -> {
            val contentStart = (parts[0] + parts[1] + parts[2]).length + 3
            val content = substring(contentStart).replace("{name}", name)
            Line(parts[0], parts[1].toDouble(), 100.0, content)
        }

        else -> {
            val content = parts.drop(1).joinToString(" ").replace("{name}", name)
            Line(parts[0], 0.0, 100.0, content)
        }
    }
}


private fun cleanName(input: File, outPut: File, start: Double, end: Double) {
    if (start > end) throw IllegalStateException("Start $start should not be after end $end")
    input.trimAudio(outPut, start, end, 44100, 0.5)
}

private fun combineFile(id: String, input: File, outPut: File) {
    input.combineAudio(outPut, id, "$id-2")
}