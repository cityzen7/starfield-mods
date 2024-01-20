import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTest {

    @Test
    fun timeFormat(){
        assertEquals("00:00:01", 1000L.formatTime())
        assertEquals("00:00:11", 11000L.formatTime())
        assertEquals("00:15:00", (1000L * 60 * 15).formatTime())
        assertEquals("12:00:00", (1000L * 60 * 60 * 12).formatTime())
        assertEquals("12:01:01", (1000L * 60 * 60 * 12 + 1000 + (60*1000)).formatTime())
    }

}