package cn.nukkit.level.format.anvil.palette

import cn.nukkit.utils.ThreadCache

/**
 * @author https://github.com/boy0001/
 */
class BitArray256 {
    private val bitsPerEntry: Int
    val data: LongArray

    constructor(bitsPerEntry: Int) {
        this.bitsPerEntry = bitsPerEntry
        val longLen = this.bitsPerEntry * 256 shr 6
        data = LongArray(longLen)
    }

    constructor(other: BitArray256) {
        bitsPerEntry = other.bitsPerEntry
        data = other.data.clone()
    }

    fun setAt(index: Int, value: Int) {
        val bitIndexStart = index * bitsPerEntry
        val longIndexStart = bitIndexStart shr 6
        val localBitIndexStart = bitIndexStart and 63
        data[longIndexStart] = data[longIndexStart] and (((1 shl bitsPerEntry) - 1).toLong() shl localBitIndexStart).inv() or value.toLong() shl localBitIndexStart
        if (localBitIndexStart > 64 - bitsPerEntry) {
            val longIndexEnd = longIndexStart + 1
            val localShiftStart = 64 - localBitIndexStart
            val localShiftEnd = bitsPerEntry - localShiftStart
            data[longIndexEnd] = data[longIndexEnd] ushr localShiftEnd shl localShiftEnd or (value.toLong() shr localShiftStart)
        }
    }

    fun getAt(index: Int): Int {
        val bitIndexStart = index * bitsPerEntry
        val longIndexStart = bitIndexStart shr 6
        val localBitIndexStart = bitIndexStart and 63
        return if (localBitIndexStart <= 64 - bitsPerEntry) {
            (data[longIndexStart] ushr localBitIndexStart and ((1 shl bitsPerEntry) - 1).toLong()).toInt()
        } else {
            val localShift = 64 - localBitIndexStart
            (data[longIndexStart] ushr localBitIndexStart or data[longIndexStart + 1] shl localShift and ((1 shl bitsPerEntry) - 1).toLong()).toInt()
        }
    }

    fun fromRaw(arr: IntArray) {
        for (i in arr.indices) {
            setAt(i, arr[i])
        }
    }

    fun grow(newBitsPerEntry: Int): BitArray256 {
        val amtGrow = newBitsPerEntry - bitsPerEntry
        if (amtGrow <= 0) return this
        val newBitArray = BitArray256(newBitsPerEntry)
        val buffer: IntArray = ThreadCache.intCache256.get()
        toRaw(buffer)
        newBitArray.fromRaw(buffer)
        return newBitArray
    }

    fun growSlow(bitsPerEntry: Int): BitArray256 {
        val newBitArray = BitArray256(bitsPerEntry)
        for (i in 0..255) {
            newBitArray.setAt(i, getAt(i))
        }
        return newBitArray
    }

    @JvmOverloads
    fun toRaw(buffer: IntArray = IntArray(256)): IntArray {
        for (i in buffer.indices) {
            buffer[i] = getAt(i)
        }
        return buffer
    }

    fun clone(): BitArray256 {
        return BitArray256(this)
    }
}