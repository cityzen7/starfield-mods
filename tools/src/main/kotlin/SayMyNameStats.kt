import kotlin.math.max

data class SayMyNameStats(val linesPerName: Int, val playerStats: MutableMap<String, SayMyNamePlayerNameStats> = mutableMapOf()) {
    private fun totalSuccesses() = playerStats.values.sumOf { it.successes }
    private fun totalTime() = playerStats.values.sumOf { it.time }

    fun print() {
        val formattedTime = totalTime()/1000f
        val divider = max(playerStats.size, 1)
        println(cyan("Processed ${totalSuccesses()}/${linesPerName * divider} total lines in $formattedTime seconds"))
    }
}

data class SayMyNamePlayerNameStats(val name: String, var successes: Int, var time: Long = 0) {
    fun print(linesPerName: Int) {
        val formattedTime = time/1000f
        if (successes != linesPerName || formattedTime > .5f) {
            println("Processed $successes/$linesPerName lines for $name in $formattedTime seconds")
        }
    }
}