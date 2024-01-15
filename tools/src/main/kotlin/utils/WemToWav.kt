package utils

import runCommand
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

fun main() {
    val config = readConfig()["audioTools"] as Map<String, Any>
    val ww2Ogg = File(config["ww2ogg"] as String)
    val wemFolder = File(config["wemFolder"] as String)
    val wemFiles = config["wemFiles"] as List<String>?

    val filesToConvert = wemFiles?.takeIf { it.isNotEmpty() }?.map { File(wemFolder.absolutePath + "/$it.wem") }
        ?: wemFolder.listFiles()!!.toList()

    filesToConvert.forEach { it.wemToWav(ww2Ogg) }
}

//Requires a folder with ww2ogg.exe and ReVorb.exe
private fun File.wemToWav(ww2oggFolder: File) {
    Files.copy(toPath(), Path(ww2oggFolder.absolutePath + "/temp.wem"), StandardCopyOption.REPLACE_EXISTING)
    println(ww2oggFolder.runCommand(listOf("wine", "ww2ogg.exe", "temp.wem", "--pcb ./packed_codebooks_aoTuV_603.bin")))
    println(ww2oggFolder.runCommand("wine ReVorb.exe temp.ogg temp1.ogg"))
    println(ww2oggFolder.runCommand("sox temp1.ogg temp.wav"))
    Files.copy(Path(ww2oggFolder.absolutePath + "/temp.wav"), File("$absolutePath/$nameWithoutExtension.wav").toPath(), StandardCopyOption.REPLACE_EXISTING)
}