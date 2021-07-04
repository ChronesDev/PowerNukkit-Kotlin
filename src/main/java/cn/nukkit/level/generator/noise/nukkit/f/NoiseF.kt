package cn.nukkit.level.generator.noise.nukkit.f

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
abstract class NoiseF {
    protected var perm: IntArray
    protected var offsetX = 0f
    protected var offsetY = 0f
    protected var offsetZ = 0f
    protected var octaves = 8f
    protected var persistence = 0f
    protected var expansion = 0f
    abstract fun getNoise2D(x: Float, z: Float): Float
    abstract fun getNoise3D(x: Float, y: Float, z: Float): Float
    @JvmOverloads
    fun noise2D(x: Float, z: Float, normalized: Boolean = false): Float {
        var x = x
        var z = z
        var result = 0f
        var amp = 1f
        var freq = 1f
        var max = 0f
        x *= expansion
        z *= expansion
        var i = 0
        while (i < octaves) {
            result += getNoise2D(x * freq, z * freq) * amp
            max += amp
            freq *= 2f
            amp *= persistence
            ++i
        }
        if (normalized) {
            result /= max
        }
        return result
    }

    @JvmOverloads
    fun noise3D(x: Float, y: Float, z: Float, normalized: Boolean = false): Float {
        var x = x
        var y = y
        var z = z
        var result = 0f
        var amp = 1f
        var freq = 1f
        var max = 0f
        x *= expansion
        y *= expansion
        z *= expansion
        var i = 0
        while (i < octaves) {
            result += getNoise3D(x * freq, y * freq, z * freq) * amp
            max += amp
            freq *= 2f
            amp *= persistence
            ++i
        }
        if (normalized) {
            result /= max
        }
        return result
    }

    fun setOffset(x: Float, y: Float, z: Float) {
        offsetX = x
        offsetY = y
        offsetZ = z
    }

    companion object {
        fun floor(x: Float): Int {
            return if (x >= 0) x.toInt() else (x - 1).toInt()
        }

        fun fade(x: Float): Float {
            return x * x * x * (x * (x * 6 - 15) + 10)
        }

        fun lerp(x: Float, y: Float, z: Float): Float {
            return y + x * (z - y)
        }

        fun linearLerp(x: Float, x1: Float, x2: Float, q0: Float, q1: Float): Float {
            return (x2 - x) / (x2 - x1) * q0 + (x - x1) / (x2 - x1) * q1
        }

        fun bilinearLerp(x: Float, y: Float, q00: Float, q01: Float, q10: Float, q11: Float, x1: Float, x2: Float, y1: Float, y2: Float): Float {
            val dx1 = (x2 - x) / (x2 - x1)
            val dx2 = (x - x1) / (x2 - x1)
            return (y2 - y) / (y2 - y1) * (dx1 * q00 + dx2 * q10) + (y - y1) / (y2 - y1) * (dx1 * q01 + dx2 * q11)
        }

        fun trilinearLerp(x: Float, y: Float, z: Float, q000: Float, q001: Float, q010: Float, q011: Float, q100: Float, q101: Float, q110: Float, q111: Float, x1: Float, x2: Float, y1: Float, y2: Float, z1: Float, z2: Float): Float {
            val dx1 = (x2 - x) / (x2 - x1)
            val dx2 = (x - x1) / (x2 - x1)
            val dy1 = (y2 - y) / (y2 - y1)
            val dy2 = (y - y1) / (y2 - y1)
            return (z2 - z) / (z2 - z1) * (dy1 * (dx1 * q000 + dx2 * q100) + dy2 * (dx1 * q001 + dx2 * q101)) + (z - z1) / (z2 - z1) * (dy1 * (dx1 * q010 + dx2 * q110) + dy2 * (dx1 * q011 + dx2 * q111))
        }

        fun grad(hash: Int, x: Float, y: Float, z: Float): Float {
            var hash = hash
            hash = hash and 15
            val u = if (hash < 8) x else y
            val v = if (hash < 4) y else if (hash == 12 || hash == 14) x else z
            return (if (hash and 1 == 0) u else -u) + if (hash and 2 == 0) v else -v
        }
    }
}