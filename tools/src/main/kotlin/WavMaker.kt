
import utils.readConfig
import java.io.File

fun main() {
    val config = readConfig()["starfield"] as Map<String, String>

    val sourceFiles = File(config["soundInput"]!!)
    sourceFiles.listFiles()!!.filter { it.isDirectory }.sortedBy { it.name }.forEach { folder ->
        println("Converting music in ${folder.name}")
        folder.listFiles()?.forEach { convertFile(it) }
    }
}

private fun convertFile(file: File){
    if (file.isFile && file.extension in listOf("mp3", "wma", "ogg")){
        val outputName = file.nameWithoutExtension + ".wav"
        println("Converting ${file.name} to $outputName")

        val cmdOut = file.parentFile.runCommand(listOf("ffmpeg", "-y", "-i", file.name, outputName))
        val newFile = File(file.parent + "/$outputName")
        if (newFile.exists()){
            file.delete()
        }
    }
}