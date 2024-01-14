package starfield

import utils.readConfig
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

private const val addFiles = true
private const val dryRun = true

fun main() {
    val config = readConfig()["starfield"] as Map<String, Any>
    val inner = config["soundSwap"] as Map<String, Any>

    val foldersToSwap = (inner["foldersToSwap"]!! as List<String>)
    val musicSource = File((inner["musicSource"]!! as String))
    val modFolder = File((inner["modFolder"]!! as String))

    val guideFolders = (config["soundKeys"]!! as List<String>)
        .flatMap { listOf(File(it)) + (File(it).listFiles()?.toList() ?: listOf()) }
        .associateBy { it.name }

    val fileNamesToMove = foldersToSwap.mapNotNull { guideFolders[it] }.flatMap { guide ->
        guide.listFiles()?.filter { it.isFile }?.map { it.name.substring(it.name.lastIndexOf("-") + 1) } ?: listOf()
    }
    println(fileNamesToMove)
    if (addFiles) addFiles(fileNamesToMove, musicSource, modFolder) else removeFiles(fileNamesToMove, modFolder)
}

private fun addFiles(fileNamesToMove: List<String>, musicSource: File, modFolder: File) {
    println("Adding ${fileNamesToMove.size}")
    fileNamesToMove
        .map { File(musicSource.absolutePath + "/$it") }
        .filter { it.exists() }
        .forEach { file ->
            if (dryRun) {
                println("Adding ${file.absolutePath} to ${modFolder.absolutePath}")
            } else {
                Files.copy(file.toPath(), Path(modFolder.absolutePath + "/${file.name}"))
            }
        }
}

private fun removeFiles(fileNamesToMove: List<String>, modFolder: File) {
    println("Deleting ${fileNamesToMove.size}")
    fileNamesToMove
        .map { File(modFolder.absolutePath + "/$it") }
        .filter { it.exists() }
        .forEach { file ->
            if (dryRun) {
                println("Deleting ${file.absolutePath}")
            } else {
                Files.delete(file.toPath())
            }
        }
}