package cn.nukkit.math

import org.junit.jupiter.api.BeforeEach

/**
 * @author joserobjr
 */
internal class NukkitRandomTest {
    var random: NukkitRandom? = null
    @Test
    fun nextRange() {
        val matched = arrayOfNulls<Boolean>(8 - 2 + 1)
        Arrays.fill(matched, Boolean.FALSE)
        for (i in 0..999) {
            val rand: Int = random.nextRange(2, 8)
            assertThat(rand).isIn(2, 3, 4, 5, 6, 7, 8)
            matched[rand - 2] = Boolean.TRUE
        }
        val expected = arrayOfNulls<Boolean>(matched.size)
        Arrays.fill(expected, Boolean.TRUE)
        assertEquals(Arrays.asList(expected), Arrays.asList(matched))
    }

    @BeforeEach
    fun setUp() {
        random = NukkitRandom(ThreadLocalRandom.current().nextLong())
    }
}