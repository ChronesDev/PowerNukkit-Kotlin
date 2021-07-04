package cn.nukkit.math

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
object NukkitMath {
    private const val ZERO_BYTE: Byte = 0
    private val ZERO_INTEGER: Integer = 0
    private const val ZERO_SHORT: Short = 0
    private const val ZERO_LONG = 0L
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isZero(storage: Number?): Boolean {
        return (ZERO_BYTE.equals(storage)
                || ZERO_INTEGER.equals(storage)
                || ZERO_SHORT.equals(storage)
                || ZERO_LONG.equals(storage)
                || BigInteger.ZERO.equals(storage))
    }

    fun floorDouble(n: Double): Int {
        val i = n.toInt()
        return if (n >= i) i else i - 1
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed math problem")
    fun ceilDouble(n: Double): Int {
        val i = n.toInt()
        return if (n > i) i + 1 else i
    }

    fun floorFloat(n: Float): Int {
        val i = n.toInt()
        return if (n >= i) i else i - 1
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed math problem")
    fun ceilFloat(n: Float): Int {
        val i = n.toInt()
        return if (n > i) i + 1 else i
    }

    fun randomRange(random: NukkitRandom): Int {
        return randomRange(random, 0)
    }

    fun randomRange(random: NukkitRandom, start: Int): Int {
        return randomRange(random, 0, 0x7fffffff)
    }

    fun randomRange(random: NukkitRandom, start: Int, end: Int): Int {
        return start + random.nextInt() % (end + 1 - start)
    }

    fun round(d: Double): Double {
        return round(d, 0)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Optimized")
    fun round(d: Double, precision: Int): Double {
        val pow: Double = Math.pow(10, precision)
        return Math.round(d * pow) as Double / pow
    }

    fun clamp(value: Double, min: Double, max: Double): Double {
        return if (value < min) min else if (value > max) max else value
    }

    fun clamp(value: Int, min: Int, max: Int): Int {
        return if (value < min) min else if (value > max) max else value
    }

    @Since("1.4.0.0-PN")
    fun clamp(value: Float, min: Float, max: Float): Float {
        return if (value < min) min else if (value > max) max else value
    }

    fun getDirection(diffX: Double, diffZ: Double): Double {
        var diffX = diffX
        var diffZ = diffZ
        diffX = Math.abs(diffX)
        diffZ = Math.abs(diffZ)
        return Math.max(diffX, diffZ)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun bitLength(data: Byte): Int {
        var data = data
        if (data < 0) {
            return 32
        }
        if (data.toInt() == 0) {
            return 1
        }
        var bits = 0
        while (data.toInt() != 0) {
            data = data ushr 1
            bits++
        }
        return bits
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun bitLength(data: Int): Int {
        var data = data
        if (data < 0) {
            return 32
        }
        if (data == 0) {
            return 1
        }
        var bits = 0
        while (data != 0) {
            data = data ushr 1
            bits++
        }
        return bits
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun bitLength(data: Long): Int {
        var data = data
        if (data < 0) {
            return 64
        }
        if (data == 0L) {
            return 1
        }
        var bits = 0
        while (data != 0L) {
            data = data ushr 1
            bits++
        }
        return bits
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun bitLength(data: BigInteger): Int {
        if (data.compareTo(BigInteger.ZERO) < 0) {
            throw UnsupportedOperationException("Negative BigIntegers are not supported (nearly infinite bits)")
        }
        return data.bitLength()
    }
}