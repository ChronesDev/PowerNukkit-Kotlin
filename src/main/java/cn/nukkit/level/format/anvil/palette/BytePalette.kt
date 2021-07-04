package cn.nukkit.level.format.anvil.palette

import io.netty.util.internal.EmptyArrays

/**
 * @author https://github.com/boy0001/
 */
class BytePalette {
    private var keys = BYTE0
    private var lastIndex = Byte.MIN_VALUE
    fun add(key: Byte) {
        keys = insert(key)
        lastIndex = Byte.MIN_VALUE
    }

    protected fun set(keys: ByteArray) {
        this.keys = keys
        lastIndex = Byte.MIN_VALUE
    }

    private fun insert(`val`: Byte): ByteArray {
        lastIndex = Byte.MIN_VALUE
        if (keys.size == 0) {
            return byteArrayOf(`val`)
        } else if (`val` < keys[0]) {
            val s = ByteArray(keys.size + 1)
            System.arraycopy(keys, 0, s, 1, keys.size)
            s[0] = `val`
            return s
        } else if (`val` > keys[keys.size - 1]) {
            val s: ByteArray = Arrays.copyOf(keys, keys.size + 1)
            s[keys.size] = `val`
            return s
        }
        val s: ByteArray = Arrays.copyOf(keys, keys.size + 1)
        for (i in s.indices) {
            if (keys[i] < `val`) {
                continue
            }
            System.arraycopy(keys, i, s, i + 1, s.size - i - 1)
            s[i] = `val`
            break
        }
        return s
    }

    fun getKey(index: Int): Byte {
        return keys[index]
    }

    fun getValue(key: Byte): Byte {
        val lastTmp = lastIndex
        val hasLast = lastTmp != Byte.MIN_VALUE
        val index: Int
        index = if (hasLast) {
            val lastKey = keys[lastTmp.toInt()]
            if (lastKey == key) return lastTmp
            if (lastKey > key) {
                binarySearch0(0, lastTmp.toInt(), key)
            } else {
                binarySearch0(lastTmp + 1, keys.size, key)
            }
        } else {
            binarySearch0(0, keys.size, key)
        }
        if (index >= keys.size || index < 0) {
            return Byte.MIN_VALUE.also { lastIndex = it }
        } else {
            return index as Byte.also{ lastIndex = it }
        }
    }

    private fun binarySearch0(fromIndex: Int, toIndex: Int, key: Byte): Int {
        var low = fromIndex
        var high = toIndex - 1
        while (low <= high) {
            val mid = low + high ushr 1
            val midVal = keys[mid]
            if (midVal < key) low = mid + 1 else if (midVal > key) high = mid - 1 else return mid // key found
        }
        return -(low + 1) // key not found.
    }

    companion object {
        private val BYTE0: ByteArray = EmptyArrays.EMPTY_BYTES
    }
}