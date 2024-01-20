import utils.Silence

data class Line(val id: String, private val content: String, val positions: MutableList<Pair<Double, Double>>) {
    fun text(playerName: String) = content.replace("{name}", playerName)

    fun getName(silences: List<Silence>): Pair<Double, Double> {
        //Try the farthest back position first, then work closer to the start
        return positions.sortedByDescending { it.first }.firstNotNullOfOrNull { (start, end) ->
            //Start: next end of silence after the start timestamp
            val nameStart = silences.filter { it.end > start }.minByOrNull { it.end }?.end

            //End: latest start of silence BEFORE the end timestamp
            val nameEnd = silences.filter { it.start < end }.maxByOrNull { it.start }?.start

            if (nameStart != null && nameEnd != null && nameStart < nameEnd) Pair(nameStart, nameEnd) else null
        } ?: throw IllegalStateException("Could not find a valid start and end position in list: $positions")
    }
}

fun List<String>.toLines(): List<Line> {
    val lines = mutableMapOf<String, Line>()
    forEach { line ->
        val parts = line.split(",")
        when {
            parts.size == 3 && parts[1].toDoubleOrNull() != null && parts[2].toDoubleOrNull() != null -> {
                lines[parts[0]]?.positions?.add(Pair(parts[1].toDouble(), parts[1].toDouble()))
            }

            parts.size == 2 && parts[1].toDoubleOrNull() != null -> {
                lines[parts[0]]?.positions?.add(Pair(parts[1].toDouble(), 100.0))
            }

            parts.getOrNull(2)?.toDoubleOrNull() != null -> {
                val contentStart = (parts[0] + parts[1] + parts[2]).length + 3
                val content = line.substring(contentStart)
                val positions = mutableListOf(Pair(parts[1].toDouble(), parts[2].toDouble()))
                lines[parts[0]] = Line(parts[0], content, positions)
            }

            parts[1].toDoubleOrNull() != null -> {
                val contentStart = (parts[0] + parts[1]).length + 2
                val content = line.substring(contentStart)
                val positions = mutableListOf(Pair(parts[1].toDouble(), 100.0))
                lines[parts[0]] = Line(parts[0], content, positions)
            }

            else -> {
                val content = parts.drop(1).joinToString(" ")
                lines[parts[0]] = Line(parts[0], content, mutableListOf(Pair(0.0, 100.0)))
            }
        }
    }
    return lines.values.toList()
}