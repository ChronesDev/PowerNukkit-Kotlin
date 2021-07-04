package cn.nukkit.level.generator.noise.nukkit.d

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class NoiseD {
    protected var perm: IntArray
    protected var offsetX = 0.0
    protected var offsetY = 0.0
    protected var offsetZ = 0.0
    protected var octaves = 8.0
    protected var persistence = 0.0
    protected var expansion = 0.0
    abstract fun getNoise2D(x: Double, z: Double): Double
    abstract fun getNoise3D(x: Double, y: Double, z: Double): Double
    @JvmOverloads
    fun noise2D(x: Double, z: Double, normalized: Boolean = false): Double {
        var x = x
        var z = z
        var result = 0.0
        var amp = 1.0
        var freq = 1.0
        var max = 0.0
        x *= expansion
        z *= expansion
        var i = 0
        while (i < octaves) {
            result += getNoise2D(x * freq, z * freq) * amp
            max += amp
            freq *= 2.0
            amp *= persistence
            ++i
        }
        if (normalized) {
            result /= max
        }
        return result
    }

    @JvmOverloads
    fun noise3D(x: Double, y: Double, z: Double, normalized: Boolean = false): Double {
        var x = x
        var y = y
        var z = z
        var result = 0.0
        var amp = 1.0
        var freq = 1.0
        var max = 0.0
        x *= expansion
        y *= expansion
        z *= expansion
        var i = 0
        while (i < octaves) {
            result += getNoise3D(x * freq, y * freq, z * freq) * amp
            max += amp
            freq *= 2.0
            amp *= persistence
            ++i
        }
        if (normalized) {
            result /= max
        }
        return result
    }

    fun setOffset(x: Double, y: Double, z: Double) {
        offsetX = x
        offsetY = y
        offsetZ = z
    }

    companion object {
        fun floor(x: Double): Int {
            return if (x >= 0) x.toInt() else (x - 1).toInt()
        }

        fun fade(x: Double): Double {
            return x * x * x * (x * (x * 6 - 15) + 10)
        }

        fun lerp(x: Double, y: Double, z: Double): Double {
            return y + x * (z - y)
        }

        fun linearLerp(x: Double, x1: Double, x2: Double, q0: Double, q1: Double): Double {
            return (x2 - x) / (x2 - x1) * q0 + (x - x1) / (x2 - x1) * q1
        }

        fun bilinearLerp(x: Double, y: Double, q00: Double, q01: Double, q10: Double, q11: Double, x1: Double, x2: Double, y1: Double, y2: Double): Double {
            val dx1 = (x2 - x) / (x2 - x1)
            val dx2 = (x - x1) / (x2 - x1)
            return (y2 - y) / (y2 - y1) * (dx1 * q00 + dx2 * q10) + (y - y1) / (y2 - y1) * (dx1 * q01 + dx2 * q11)
        }

        fun trilinearLerp(x: Double, y: Double, z: Double, q000: Double, q001: Double, q010: Double, q011: Double, q100: Double, q101: Double, q110: Double, q111: Double, x1: Double, x2: Double, y1: Double, y2: Double, z1: Double, z2: Double): Double {
            val dx1 = (x2 - x) / (x2 - x1)
            val dx2 = (x - x1) / (x2 - x1)
            val dy1 = (y2 - y) / (y2 - y1)
            val dy2 = (y - y1) / (y2 - y1)
            return (z2 - z) / (z2 - z1) * (dy1 * (dx1 * q000 + dx2 * q100) + dy2 * (dx1 * q001 + dx2 * q101)) + (z - z1) / (z2 - z1) * (dy1 * (dx1 * q010 + dx2 * q110) + dy2 * (dx1 * q011 + dx2 * q111))
        }

        fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
            var hash = hash
            hash = hash and 15
            val u = if (hash < 8) x else y
            val v = if (hash < 4) y else if (hash == 12 || hash == 14) x else z
            return (if (hash and 1 == 0) u else -u) + if (hash and 2 == 0) v else -v
        }
    }
}