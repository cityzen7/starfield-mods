import utils.combineAudio
import utils.getSilences
import utils.readConfig
import utils.trimAudio
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.math.max

const val onlyOneName = false
const val exitOnError = false
const val skipExisting = true
val onlyLine: String = ""


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
        val lines = File("input/sayMyName/$character.txt").readLines().map { it.toLine() }
        val stats = mutableMapOf<String, Int>()

        playerNames.forEach { playerName ->
            File(workingDir.absolutePath + "/out/$playerName/").mkdirs()
            File(coquiDir.absolutePath + "/out/$playerName/").mkdirs()
            val filteredLines = lines.let { if (onlyLine.isNotEmpty()) it.filter { line -> line.id == onlyLine } else it }
                .filter { !skipExisting || !File("${workingDir.absolutePath}/out/$playerName/${it.id}.wav").exists() }
            stats[playerName] = lines.size - filteredLines.size

                filteredLines.forEach { line ->
                    try {
                        val ttsOut = File(coquiDir.absolutePath + "/out/$playerName/${line.id}.wav")
                        if (!ttsOut.exists()) {
                            println("Generating ${line.id}: '${line.text(playerName)}'")
                            processLine(line.id, line.text(playerName), coquiDir, voices, "./out/$playerName/${line.id}.wav")
                        }
                    } catch (e: Exception) {
                        println(red("Failed to generate $playerName ${line.id}: ${e.message}"))
                    }
                }

                filteredLines.forEach { line ->
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
                        stats[playerName] = stats.getOrDefault(playerName, 0) + 1
                    } catch (e: Exception) {
                        println(red("Failed to process $playerName ${line.id}: ${e.message ?: e.stackTraceToString()}"))
                        if (exitOnError) throw IllegalStateException("Encountered Error")
                    }
                    tempFile2.delete()
                    tempFile3.delete()
                }
            printStats(stats, lines.size, playerName)
        }
        printStats(stats, lines.size)
    }
}



private fun cleanName(input: File, outPut: File, start: Double, end: Double) {
    if (start > end) throw IllegalStateException("Start $start should not be after end $end")
    input.trimAudio(outPut, start, end, 44100, 0.5)
}

private fun combineFile(id: String, input: File, outPut: File) {
    input.combineAudio(outPut, id, "$id-2")
}

private fun printStats(stats: Map<String, Int>, lineCountForCharacter: Int){
    val divider = max(stats.size, 1)
    println("Processed ${stats.values.sum()}/${lineCountForCharacter*divider} total lines")
}

private fun printStats(stats: Map<String, Int>, lineCountForCharacter: Int, name: String){
    val numerator = stats[name] ?: 0
    println("Processed $numerator/$lineCountForCharacter lines for $name")
}