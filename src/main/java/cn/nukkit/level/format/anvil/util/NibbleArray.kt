package cn.nukkit.level.format.anvil.util

import com.google.common.base.Preconditions

class NibbleArray : Cloneable {
    val data: ByteArray

    constructor(length: Int) {
        data = ByteArray(length / 2)
    }

    constructor(array: ByteArray) {
        data = array
    }

    operator fun get(index: Int): Byte {
        Preconditions.checkElementIndex(index, data.size * 2)
        val `val` = data[index / 2]
        return if (index and 1 == 0) {
            (`val` and 0x0f) as Byte
        } else {
            (`val` and 0xf0 ushr 4)
        }
    }

    operator fun set(index: Int, value: Byte) {
        var value = value
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.")
        Preconditions.checkElementIndex(index, data.size * 2)
        value = value and 0xf
        val half = index / 2
        val previous = data[half]
        if (index and 1 == 0) {
            data[half] = (previous and 0xf0 or value) as Byte
        } else {
            data[half] = (previous and 0x0f or value shl 4) as Byte
        }
    }

    fun fill(value: Byte) {
        var value = value
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.")
        value = value and 0xf
        for (i in data.indices) {
            data[i] = (value shl 4 or value) as Byte
        }
    }

    fun copyFrom(bytes: ByteArray) {
        Preconditions.checkNotNull(bytes, "bytes")
        Preconditions.checkArgument(bytes.size == data.size, "length of provided byte array is %s but expected %s", bytes.size,
                data.size)
        System.arraycopy(bytes, 0, data, 0, data.size)
    }

    fun copyFrom(array: NibbleArray) {
        Preconditions.checkNotNull(array, "array")
        copyFrom(array.data)
    }

    fun copy(): NibbleArray {
        return NibbleArray(data.clone())
    }
}