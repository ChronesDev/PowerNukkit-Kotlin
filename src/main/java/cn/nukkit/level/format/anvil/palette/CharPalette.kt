package cn.nukkit.level.format.anvil.palette

import io.netty.util.internal.EmptyArrays

/**
 * @author https://github.com/boy0001/
 */
class CharPalette {
    private var keys: CharArray = EmptyArrays.EMPTY_CHARS
    private var lastIndex: Char = Character.MAX_VALUE
    fun add(key: Char) {
        keys = insert(key)
        lastIndex = Character.MAX_VALUE
    }

    fun set(keys: CharArray) {
        this.keys = keys
        lastIndex = Character.MAX_VALUE
    }

    private fun insert(`val`: Char): CharArray {
        lastIndex = Character.MAX_VALUE
        if (keys.size == 0) {
            return charArrayOf(`val`)
        } else if (`val` < keys[0]) {
            val s = CharArray(keys.size + 1)
            System.arraycopy(keys, 0, s, 1, keys.size)
            s[0] = `val`
            return s
        } else if (`val` > keys[keys.size - 1]) {
            val s: CharArray = Arrays.copyOf(keys, keys.size + 1)
            s[keys.size] = `val`
            return s
        }
        val s: CharArray = Arrays.copyOf(keys, keys.size + 1)
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

    fun getKey(index: Int): Char {
        return keys[index]
    }

    fun getValue(key: Char): Char {
        val lastTmp = lastIndex
        val hasLast = lastTmp != Character.MAX_VALUE
        val index: Int
        index = if (hasLast) {
            val lastKey = keys[lastTmp.toInt()]
            if (lastKey == key) return lastTmp
            if (lastKey > key) {
                binarySearch0(0, lastTmp.toInt(), key)
            } else {
                binarySearch0(lastTmp.toInt() + 1, keys.size, key)
            }
        } else {
            binarySearch0(0, keys.size, key)
        }
        if (index >= keys.size || index < 0) {
            return Character.MAX_VALUE.also { lastIndex = it }
        } else {
            return index as Char.also{ lastIndex = it }
        }
    }

    private fun binarySearch0(fromIndex: Int, toIndex: Int, key: Char): Int {
        var low = fromIndex
        var high = toIndex - 1
        while (low <= high) {
            val mid = low + high ushr 1
            val midVal = keys[mid]
            if (midVal < key) low = mid + 1 else if (midVal > key) high = mid - 1 else return mid // key found
        }
        return -(low + 1) // key not found.
    }
}