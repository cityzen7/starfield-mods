data class Recipe(
    val character: String,
    val playerName: String,
    val lines: List<Line>,
    val stats: SayMyNamePlayerNameStats = SayMyNamePlayerNameStats(playerName, 0),
    val overrides: Map<String, String> = mutableMapOf()
)

fun List<Recipe>.chunkedByLines(minLineCount: Int): List<List<Recipe>> {
    return this.fold(mutableListOf(mutableListOf())) { acc: MutableList<MutableList<Recipe>>, recipe: Recipe ->
        val runningTotal = acc.last().sumOf { it.lines.size }
        acc.apply {
            if (runningTotal < minLineCount) {
                acc.last().add(recipe)
            } else {
                acc.add(mutableListOf(recipe))
            }
        }
    }
}