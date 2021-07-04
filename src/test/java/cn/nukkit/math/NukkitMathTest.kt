package cn.nukkit.math

import org.junit.jupiter.api.Test

/**
 * @author joserobjr
 */
internal class NukkitMathTest {
    @Test
    fun floorDouble() {
        var i = -5.0
        while (i <= 6) {
            assertEquals(Math.floor(i) as Int, NukkitMath.floorDouble(i))
            i += 0.001
        }
    }

    @Test
    fun ceilDouble() {
        var i = -5.0
        while (i <= 6) {
            assertEquals(Math.ceil(i) as Int, NukkitMath.ceilDouble(i))
            i += 0.001
        }
    }

    @Test
    fun floorFloat() {
        var i = -5f
        while (i <= 6) {
            assertEquals(Math.floor(i) as Int, NukkitMath.floorFloat(i))
            i += 0.001f
        }
    }

    @Test
    fun ceilFloat() {
        var i = -5f
        while (i <= 6) {
            assertEquals(Math.ceil(i) as Int, NukkitMath.ceilFloat(i))
            i += 0.001f
        }
    }
}