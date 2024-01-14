import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

const val THUMBS_UP = "\uD83D\uDC4D"
const val THUMBS_DOWN = "\uD83D\uDC4E"
const val FOLDER = "\uD83D\uDCC1"
val ENABLED = Character.toString(0x1f517)!!
val UPDATE = Character.toString(0x1f4e9)!!
private const val ANSI_RESET = "\u001B[0m";

fun red(text: String) = "\u001B[31m$text$ANSI_RESET"
fun green(text: String) = "\u001B[32m$text$ANSI_RESET"
fun yellow(text: String) = "\u001B[33m$text$ANSI_RESET"
fun blue(text: String) = "\u001B[34m$text$ANSI_RESET"
fun purple(text: String) = "\u001B[35m$text$ANSI_RESET"
fun cyan(text: String) = "\u001B[36m$text$ANSI_RESET"
fun white(text: String) = "\u001B[37m$text$ANSI_RESET"
fun grayBG(text: String) = "\u001B[100m$text$ANSI_RESET"

fun File.runCommand(command: String): String? {
    val parts = command.split("\\s".toRegex())
    return runCommand(parts)
}

fun File.runCommand(command: List<String>): String? {
    return try {
        val proc = ProcessBuilder(*command.toTypedArray())
            .directory(this)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start()

        proc.waitFor(60, TimeUnit.MINUTES)
        proc.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

suspend fun File.runCommandAsync(command: String): String {
    return withContext(Dispatchers.IO) {
        async {
            val parts = command.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(this@runCommandAsync)
                .start()
            proc.inputStream.reader(Charsets.UTF_8).use {
                it.readText()
            }
        }.await()
    }
}