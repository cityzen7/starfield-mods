import kotlin.math.max

data class SayMyNameStats(val linesPerName: Int, val playerStats: MutableMap<String, SayMyNamePlayerNameStats> = mutableMapOf()) {
    private fun totalSuccesses() = playerStats.values.sumOf { it.successes }
    private fun totalTime() = playerStats.values.sumOf { it.time }

    fun print() {
        val formattedTime = totalTime() / 1000f
        val divider = max(playerStats.size, 1)
        println(cyan("Processed ${totalSuccesses()}/${linesPerName * divider} total lines in $formattedTime seconds"))
    }
}

data class SayMyNamePlayerNameStats(val name: String, var successes: Int, var time: Long = 0) {
    fun print(linesPerName: Int) {
        if (successes != linesPerName || time > 500f) {
            println("Processed $successes/$linesPerName lines for $name in ${time.formatTime()} hrs:min:sec")
        }
    }
}

fun Long.formatTime(): String {
    val seconds = ((this % (60 * 1000)) / 1000).toString().padStart(2, '0')
    val minutes = ((this  % (60 * 60 * 1000))/ (60 * 1000)).toString().padStart(2, '0')
    val hours = (this / (60 * 60 * 1000)).toString().padStart(2, '0')
    return "$hours:$minutes:$seconds"
}