import utils.Silence
import kotlin.math.max

data class Line(val id: String, private val content: String, val silencesFromEnd: Int = 1) {
    fun text(playerName: String, overrides: Map<String, String>): String {
        return overrides.getOrDefault(id, content).replace("{name}", playerName)
    }

    fun getName(silences: List<Silence>): Pair<Double, Double> {
        //Use preferred config if possible, but if that's invalid, use the earliest silence
        val start = max(silences.size - 1 - silencesFromEnd, 0)

        val end = (silences.size - silencesFromEnd).let { if (it == start) it + 1 else it }

        val nameStart = silences.getOrNull(start)?.end ?: 0.0
        val nameEnd = silences.getOrNull(end)?.start ?: 0.0

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

fun List<String>.toLineOverrides(): Map<String, Map<String, String>> {
    val overrides = mutableMapOf<String, MutableMap<String, String>>()
    forEach { line ->
        val parts = line.split(",")
        val name = parts[0]
        val id = parts[1]
        val content = if (parts.size == 2) "thank you: {name}." else {
            val contentStart = (name + id).length + 2
            line.substring(contentStart)
        }
        overrides.putIfAbsent(name, mutableMapOf())
        overrides[name]?.put(id, content)
    }
    return overrides
}