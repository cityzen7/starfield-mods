import utils.Silence
import java.lang.IllegalStateException
import kotlin.math.max

data class Line(val id: String, private val content: String, val silencesFromEnd: Int = 1) {
    fun text(playerName: String) = content.replace("{name}", playerName)

    fun getName(silences: List<Silence>): Pair<Double, Double> {
        //Use preferred config if possible, but if that's invalid, use the earliest silence
        val start = max(silences.size - 1 - silencesFromEnd, 0)

        val end = (silences.size - silencesFromEnd).let { if (it == start) it + 1 else it }

        val nameStart = silences.getOrNull(start)?.end ?: 0.0
        val nameEnd = silences.getOrNull(end)?.start ?: 0.0

        if (nameStart >= nameEnd) throw IllegalStateException("Could not find proper start and end")

        return Pair(nameStart, nameEnd)
    }
}

fun List<String>.toLines(): List<Line> {
    return map { line ->
        val parts = line.split(",")
        when {
            parts[1].toIntOrNull() != null -> {
                val contentStart = (parts[0] + parts[1]).length + 2
                val content = line.substring(contentStart)
                Line(parts[0], content, parts[1].toInt())
            }

            else -> {
                val content = parts.drop(1).joinToString(" ")
                Line(parts[0], content)
            }
        }
    }
}