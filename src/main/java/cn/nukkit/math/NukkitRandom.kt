package cn.nukkit.math

import java.nio.ByteBuffer

/**
 * @author Angelic47 (Nukkit Project)
 */
class NukkitRandom @JvmOverloads constructor(seeds: Long = -1) {
    protected var seed: Long = 0
    fun setSeed(seeds: Long) {
        val crc32 = CRC32()
        val buffer: ByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(seeds.toInt())
        crc32.update(buffer.array())
        seed = crc32.getValue()
    }

    fun nextSignedInt(): Int {
        val t = ((seed * 65535 + 31337).toInt() shr 8) + 1337
        seed = seed xor t.toLong()
        return t
    }

    fun nextInt(): Int {
        return nextSignedInt() and 0x7fffffff
    }

    fun nextDouble(): Double {
        return nextInt().toDouble() / 0x7fffffff
    }

    fun nextFloat(): Float {
        return nextInt().toFloat() / 0x7fffffff
    }

    fun nextSignedFloat(): Float {
        return nextInt().toFloat() / 0x7fffffff
    }

    fun nextSignedDouble(): Double {
        return nextSignedInt().toDouble() / 0x7fffffff
    }

    fun nextBoolean(): Boolean {
        return nextSignedInt() and 0x01 == 0
    }

    @JvmOverloads
    fun nextRange(start: Int = 0, end: Int = 0x7fffffff): Int {
        return start + nextInt() % (end + 1 - start)
    }

    fun nextBoundedInt(bound: Int): Int {
        return if (bound == 0) 0 else nextInt() % bound
    }

    init {
        var seeds = seeds
        if (seeds == -1L) {
            seeds = System.currentTimeMillis() / 1000L
        }
        setSeed(seeds)
    }
}