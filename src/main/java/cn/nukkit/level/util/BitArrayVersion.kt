package cn.nukkit.level.util

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

enum class BitArrayVersion(bits: Int, entriesPerWord: Int, next: BitArrayVersion?) {
    V16(16, 2, null), V8(8, 4, V16), V6(6, 5, V8),  // 2 bit padding
    V5(5, 6, V6),  // 2 bit padding
    V4(4, 8, V5), V3(3, 10, V4),  // 2 bit padding
    V2(2, 16, V3), V1(1, 32, V2);

    val id: Byte
    val entriesPerWord: Byte
    val maxEntryValue: Int
    val next: BitArrayVersion?
    fun createPalette(size: Int): BitArray {
        return this.createPalette(size, IntArray(getWordsForSize(size)))
    }

    fun getWordsForSize(size: Int): Int {
        return size / entriesPerWord + if (size % entriesPerWord == 0) 0 else 1
    }

    operator fun next(): BitArrayVersion? {
        return next
    }

    fun createPalette(size: Int, words: IntArray): BitArray {
        return if (this == V3 || this == V5 || this == V6) {
            // Padded palettes aren't able to use bitwise operations due to their padding.
            PaddedBitArray(this, size, words)
        } else {
            Pow2BitArray(this, size, words)
        }
    }

    companion object {
        operator fun get(version: Int, read: Boolean): BitArrayVersion {
            for (ver in values()) {
                if (!read && ver.entriesPerWord <= version || read && ver.id.toInt() == version) {
                    return ver
                }
            }
            throw IllegalArgumentException("Invalid palette version: $version")
        }
    }

    init {
        id = bits.toByte()
        this.entriesPerWord = entriesPerWord.toByte()
        maxEntryValue = (1 shl id.toInt()) - 1
        this.next = next
    }
}