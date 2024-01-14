import utils.readConfig
import java.io.File
import java.nio.file.Files

private const val dryRun = true

fun main() {
    val config = readConfig()["starfield"] as Map<String, Any>
    val sourceFiles = File(config["soundInput"]!! as String)
    val guideFolders = (config["soundKeys"]!! as List<String>)
        .flatMap { listOf(File(it)) + (File(it).listFiles()?.toList() ?: listOf()) }
        .associateBy { it.name }
    val keyMatchers = (config["soundKeyMatchers"]!! as Map<String, List<String>>)
        .entries.flatMap { (folder, keys) -> keys.map { it to folder } }
        .toMap()
    val directKeys = (config["directKeys"]!! as Map<String, List<String>>)

    val output = File(sourceFiles.path + "/out")
    if (output.exists()) output.deleteRecursively()
    output.mkdir()
    sourceFiles.prepMusic(guideFolders, keyMatchers, directKeys, output)
}

private fun File.prepMusic(guideFolders: Map<String, File>, keyMatchers: Map<String, String>, directKeys: Map<String, List<String>>, output: File) {
    listFiles()!!.filter { it.isDirectory && it.listFiles()?.isNotEmpty() ?: false }.forEach { folder ->
        val folderMatch = keyMatchers[folder.name]
        val directMatches = directKeys[folder.name]
        val guideFolder = guideFolders[folderMatch ?: folder.name]
        val subFiles = folder.listFiles()!!
        val hasSubDirectories = subFiles.any { it.isDirectory }
        val hasMusic = subFiles.any { it.isFile }

        when {
            guideFolder != null && guideFolder.isDirectory && hasMusic -> moveFiles(folder, guideFolder, directMatches, folderMatch != null, output)
            hasSubDirectories -> folder.prepMusic(guideFolders, keyMatchers, directKeys, output)
            else -> println(yellow("Could not find match for ${folder.name}"))
        }
    }
}

private fun moveFiles(sourceFolder: File, guideFolder: File, directMatches: List<String>?, useFolderMatch: Boolean, output: File) {
    val sourceFiles = sourceFolder.listFiles()?.filter { it.isFile } ?: listOf()
    val guideFiles = directMatches ?: guideFolder.listFiles()
        ?.filter { it.isFile && (!useFolderMatch || it.name.contains(sourceFolder.name)) }?.map { it.nameWithoutExtension }

    val folderMatchString = if (useFolderMatch) " (${sourceFolder.name})" else ""
    println(cyan("Processing ${sourceFiles.size} files in ${guideFolder.name}$folderMatchString"))
    println(sourceFolder.path)
    if (sourceFiles.size > (guideFiles?.size ?: 0)) {
        println(yellow("More input (${sourceFiles.size}) than game files (${guideFiles?.size ?: 0})"))
    }
    guideFiles?.forEachIndexed { i, guide ->
        val sourceMatch = sourceFiles.getOrNull(i)
        if (sourceMatch != null && sourceMatch.isFile) {
            val fileKey = guide.let { it.substring(it.lastIndexOf("-") + 1, it.length) }
            val outPath = File(output.path + "/$fileKey." + sourceMatch.extension).toPath()
            if (dryRun) {
                println("Move ${sourceMatch.name} to $fileKey.${sourceMatch.extension}")
            } else {
                Files.copy(sourceMatch.toPath(), outPath)
            }
        }
    }
    println()
}
