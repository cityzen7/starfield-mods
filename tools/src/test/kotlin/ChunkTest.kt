import kotlin.test.Test
import kotlin.test.assertEquals

class ChunkTest {

    @Test
    fun grabsMinimum(){
        val recipes = listOf(
            Recipe("bob", lines("one", "two")),
            Recipe("bob", lines("one", "three", "four")),
        )
        val actual = recipes.chunkedByLines(1)
        assertEquals(2, actual.size)
        assertEquals(1, actual.first().size)
    }

    @Test
    fun grabsExact(){
        val recipes = listOf(
            Recipe("bob", lines("one", "two")),
            Recipe("bob", lines("one", "three", "four")),
        )
        val actual = recipes.chunkedByLines(2)
        assertEquals(2, actual.size)
        assertEquals(1, actual.first().size)
    }

    @Test
    fun grabsAnotherToMeetMinimum(){
        val recipes = listOf(
            Recipe("bob", lines("one", "two")),
            Recipe("bob", lines("one", "two", "three", "four")),
        )
        val actual = recipes.chunkedByLines(3)
        assertEquals(1, actual.size)
        assertEquals(2, actual.first().size)
    }

    private fun lines(vararg text: String) = text.map { Line(it, it) }

}