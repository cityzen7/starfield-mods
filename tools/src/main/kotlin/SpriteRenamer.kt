import utils.readConfig
import java.io.File

private const val dryRun = false

fun main() {
    val config = readConfig()["starfield"] as Map<String, String>

    val root = File(config["sprites"]!!)
    val output = File(root.absolutePath + "/output")
    if (!output.exists()) output.mkdir()
    organizeSprites(root, output)
}

private fun organizeSprites(root: File, output: File) {
    root.listFiles()!!
        .filter { it.isDirectory && it.name.startsWith("DefineSprite") }
        .forEach { folder ->
            val name = folder.name.substring(folder.name.lastIndexOf("_") + 1)
                .let { if (it.toIntOrNull() != null) "sprite-$it" else it }
            folder.listFiles()!!.filter { it.name.endsWith("png") }.forEach { image ->
                val newName = output.absolutePath + "/$name-${image.name}"
                if (dryRun) {
                    println(newName)
                } else {
                    image.copyTo(File(newName), true)
                }
            }
        }
}
