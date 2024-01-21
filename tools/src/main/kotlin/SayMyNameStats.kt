import kotlin.math.max

data class SayMyNameStats(val linesPerName: Int, val playerStats: MutableMap<String, SayMyNamePlayerNameStats> = mutableMapOf()) {
    private val start = System.currentTimeMillis()
    private fun totalSuccesses() = playerStats.values.sumOf { it.successes }

    fun print() {
        val divider = max(playerStats.size, 1)
        val time = System.currentTimeMillis() - start
        println(cyan("Processed ${totalSuccesses()}/${linesPerName * divider} total lines in ${time.formatTime()} (hrs:min:sec)"))
    }
}

data class SayMyNamePlayerNameStats(val name: String, var successes: Int) {
    fun print(linesPerName: Int) {
        if (successes != linesPerName) {
            println("Processed $successes/$linesPerName lines for $name")
        }
    }
}

fun Long.formatTime(): String {
    val seconds = ((this % (60 * 1000)) / 1000).toString().padStart(2, '0')
    val minutes = ((this  % (60 * 60 * 1000))/ (60 * 1000)).toString().padStart(2, '0')
    val hours = (this / (60 * 60 * 1000)).toString().padStart(2, '0')
    return "$hours:$minutes:$seconds"
}