package cn.nukkit.level.format.anvil.palette

import cn.nukkit.utils.ThreadCache

/**
 * @author https://github.com/boy0001/
 */
class BitArray4096(private val bitsPerEntry: Int) {
    private val maxSeqLocIndex: Int
    private val maxEntryValue: Int
    private val data: LongArray
    fun setAt(index: Int, value: Int) {
        if (data.size == 0) return
        val bitIndexStart = index * bitsPerEntry
        val longIndexStart = bitIndexStart shr 6
        val localBitIndexStart = bitIndexStart and 63
        data[longIndexStart] = data[longIndexStart] and (maxEntryValue.toLong() shl localBitIndexStart).inv() or value.toLong() shl localBitIndexStart
        if (localBitIndexStart > maxSeqLocIndex) {
            val longIndexEnd = longIndexStart + 1
            val localShiftStart = 64 - localBitIndexStart
            val localShiftEnd = bitsPerEntry - localShiftStart
            data[longIndexEnd] = data[longIndexEnd] ushr localShiftEnd shl localShiftEnd or (value.toLong() shr localShiftStart)
        }
    }

    fun getAt(index: Int): Int {
        if (data.size == 0) return 0
        val bitIndexStart = index * bitsPerEntry
        val longIndexStart = bitIndexStart shr 6
        val localBitIndexStart = bitIndexStart and 63
        return if (localBitIndexStart <= maxSeqLocIndex) {
            (data[longIndexStart] ushr localBitIndexStart and maxEntryValue.toLong()).toInt()
        } else {
            val localShift = 64 - localBitIndexStart
            (data[longIndexStart] ushr localBitIndexStart or data[longIndexStart + 1] shl localShift and maxEntryValue.toLong()).toInt()
        }
    }

    fun fromRawSlow(arr: CharArray) {
        for (i in arr.indices) {
            setAt(i, arr[i].toInt())
        }
    }

    fun fromRaw(arr: CharArray) {
        val data = data
        val dataLength = data.size
        val bitsPerEntry = bitsPerEntry
        val maxEntryValue = maxEntryValue
        val maxSeqLocIndex = maxSeqLocIndex
        var localStart = 0
        var lastVal: Char
        var arrI = 0
        var l: Long = 0
        var nextVal: Long
        for (i in 0 until dataLength) {
            while (localStart <= maxSeqLocIndex) {
                lastVal = arr[arrI++]
                l = l or (lastVal.toLong() shl localStart)
                localStart += bitsPerEntry
            }
            if (localStart < 64) {
                if (i != dataLength - 1) {
                    lastVal = arr[arrI++]
                    val shift = 64 - localStart
                    nextVal = (lastVal.toInt() shr shift).toLong()
                    l = l or (lastVal.toLong() - (nextVal shl shift) shl localStart)
                    data[i] = l
                    l = nextVal
                    data[i + 1] = l
                    localStart -= maxSeqLocIndex
                }
            } else {
                localStart = 0
                data[i] = l
                l = 0
            }
        }
    }

    fun grow(newBitsPerEntry: Int): BitArray4096 {
        val amtGrow = newBitsPerEntry - bitsPerEntry
        if (amtGrow <= 0) return this
        val newBitArray = BitArray4096(newBitsPerEntry)
        val buffer: CharArray = ThreadCache.charCache4096.get()
        toRaw(buffer)
        newBitArray.fromRaw(buffer)
        return newBitArray
    }

    fun growSlow(bitsPerEntry: Int): BitArray4096 {
        val newBitArray = BitArray4096(bitsPerEntry)
        for (i in 0..4095) {
            newBitArray.setAt(i, getAt(i))
        }
        return newBitArray
    }

    fun toRawSlow(): CharArray {
        val arr = CharArray(4096)
        for (i in arr.indices) {
            arr[i] = getAt(i).toChar()
        }
        return arr
    }

    fun toRaw(): CharArray {
        return toRaw(CharArray(4096))
    }

    protected fun toRaw(buffer: CharArray): CharArray {
        val data = data
        val dataLength = data.size
        val bitsPerEntry = bitsPerEntry
        val maxEntryValue = maxEntryValue
        val maxSeqLocIndex = maxSeqLocIndex
        var localStart = 0
        var lastVal: Char
        var arrI = 0
        var l: Long
        for (i in 0 until dataLength) {
            l = data[i]
            while (localStart <= maxSeqLocIndex) {
                lastVal = (l ushr localStart and maxEntryValue.toLong()).toChar()
                buffer[arrI++] = lastVal
                localStart += bitsPerEntry
            }
            if (localStart < 64) {
                if (i != dataLength - 1) {
                    lastVal = (l ushr localStart).toChar()
                    localStart -= maxSeqLocIndex
                    l = data[i + 1]
                    val localShift = bitsPerEntry - localStart
                    lastVal = (lastVal.toLong() or (l shl localShift)).toChar()
                    lastVal = (lastVal.toInt() and maxEntryValue).toChar()
                    buffer[arrI++] = lastVal
                }
            } else {
                localStart = 0
            }
        }
        return buffer
    }

    init {
        maxSeqLocIndex = 64 - bitsPerEntry
        maxEntryValue = (1 shl bitsPerEntry) - 1
        val longLen = bitsPerEntry * 4096 shr 6
        data = LongArray(longLen)
    }
}