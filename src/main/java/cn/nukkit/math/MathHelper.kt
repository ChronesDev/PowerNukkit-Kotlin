package cn.nukkit.math

import java.util.Random

object MathHelper {
    private val a = FloatArray(65536)
    fun sqrt(paramFloat: Float): Float {
        return Math.sqrt(paramFloat)
    }

    fun sin(paramFloat: Float): Float {
        return a[(paramFloat * 10430.378f).toInt() and 0xFFFF]
    }

    fun cos(paramFloat: Float): Float {
        return a[(paramFloat * 10430.378f + 16384.0f).toInt() and 0xFFFF]
    }

    fun sin(paramFloat: Double): Float {
        return a[(paramFloat * 10430.378f).toInt() and 0xFFFF]
    }

    fun cos(paramFloat: Double): Float {
        return a[(paramFloat * 10430.378f + 16384.0f).toInt() and 0xFFFF]
    }

    fun floor(d0: Double): Int {
        val i = d0.toInt()
        return if (d0 < i.toDouble()) i - 1 else i
    }

    fun floor_double_long(d: Double): Long {
        val l = d.toLong()
        return if (d >= l.toDouble()) l else l - 1L
    }

    fun floor_float_int(f: Float): Int {
        val i = f.toInt()
        return if (f >= i) i else i - 1
    }

    fun abs(number: Int): Int {
        return if (number > 0) {
            number
        } else {
            -number
        }
    }

    fun log2(bits: Int): Int {
        return Integer.SIZE - Integer.numberOfLeadingZeros(bits)
    }

    /**
     * Returns a random number between min and max, inclusive.
     *
     * @param random The random number generator.
     * @param min    The minimum value.
     * @param max    The maximum value.
     * @return A random number between min and max, inclusive.
     */
    fun getRandomNumberInRange(random: Random, min: Int, max: Int): Int {
        return min + random.nextInt(max - min + 1)
    }

    fun max(first: Double, second: Double, third: Double, fourth: Double): Double {
        if (first > second && first > third && first > fourth) {
            return first
        }
        return if (second > third && second > fourth) {
            second
        } else Math.max(third, fourth)
    }

    fun ceil(floatNumber: Float): Int {
        val truncated = floatNumber.toInt()
        return if (floatNumber > truncated) truncated + 1 else truncated
    }

    fun clamp(check: Int, min: Int, max: Int): Int {
        return if (check > max) max else if (check < min) min else check
    }

    fun clamp(num: Float, min: Float, max: Float): Float {
        return if (num > max) max else if (num < min) min else num
    }

    fun denormalizeClamp(lowerBnd: Double, upperBnd: Double, slide: Double): Double {
        return if (slide < 0.0) lowerBnd else if (slide > 1.0) upperBnd else lowerBnd + (upperBnd - lowerBnd) * slide
    }

    fun denormalizeClamp(lowerBnd: Float, upperBnd: Float, slide: Float): Float {
        return if (slide < 0.0f) lowerBnd else if (slide > 1.0f) upperBnd else lowerBnd + (upperBnd - lowerBnd) * slide
    }

    init {
        for (i in 0..65535) a[i] = Math.sin(i * 3.141592653589793 * 2.0 / 65536.0) as Float
    }
}