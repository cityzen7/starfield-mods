
data class Line(val id: String, val start: Double, val end: Double, private val content: String) {
    fun text(playerName: String) = content.replace("{name}", playerName)
}


fun String.toLine(): Line {
    val parts = split(",")

    return when {
        parts.getOrNull(2)?.toDoubleOrNull() != null -> {
            val contentStart = (parts[0] + parts[1] + parts[2]).length + 3
            val content = substring(contentStart)
            Line(parts[0], parts[1].toDouble(), parts[2].toDouble(), content)
        }

        parts[1].toDoubleOrNull() != null -> {
            val contentStart = (parts[0] + parts[1]).length + 2
            val content = substring(contentStart)
            Line(parts[0], parts[1].toDouble(), 100.0, content)
        }

        else -> {
            val content = parts.drop(1).joinToString(" ")
            Line(parts[0], 0.0, 100.0, content)
        }
    }
}