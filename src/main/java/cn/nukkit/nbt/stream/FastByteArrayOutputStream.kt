package cn.nukkit.nbt.stream
/*
 * fastutil: Fast & compact type-specific collections for Java
 *
 * Copyright (C) 2003-2011 Sebastiano Vigna
 *
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the Free
 *  Software Foundation; either version 2.1 of the License, or (at your option)
 *  any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */import java.io.IOException

/** Simple, fast byte-array output stream that exposes the backing array.
 *
 * <P>[java.io.ByteArrayOutputStream] is nice, but to get its content you
 * must generate each time a new object. This doesn't happen here.
 *
</P> * <P>This class will automatically enlarge the backing array, doubling its
 * size whenever new space is needed. The [.reset] method will
 * mark the content as empty, but will not decrease the capacity
 *
 * @author Sebastiano Vigna
</P> */
class FastByteArrayOutputStream : OutputStream {
    /** The array backing the output stream.  */
    var array: ByteArray

    /** The number of valid bytes in [.array].  */
    var length = 0

    /** The current writing position.  */
    private var position = 0
    /** Creates a new array output stream with a given initial capacity.
     *
     * @param initialCapacity the initial length of the backing array.
     */
    /** Creates a new array output stream with an initial capacity of [.DEFAULT_INITIAL_CAPACITY] bytes.  */
    @JvmOverloads
    constructor(initialCapacity: Int = DEFAULT_INITIAL_CAPACITY) {
        array = ByteArray(initialCapacity)
    }

    /** Creates a new array output stream wrapping a given byte array.
     *
     * @param a the byte array to wrap.
     */
    constructor(a: ByteArray) {
        array = a
    }

    /** Marks this array output stream as empty.  */
    fun reset(): FastByteArrayOutputStream {
        length = 0
        position = 0
        return this
    }

    fun write(b: Int) {
        if (position == length) {
            length++
            if (position == array.size) array = grow(array, length)
        }
        array[position++] = b.toByte()
    }

    @Throws(IOException::class)
    fun write(b: ByteArray?, off: Int, len: Int) {
        if (position + len > array.size) array = grow(array, position + len, position)
        System.arraycopy(b, off, array, position, len)
        if (position + len > length) {
            position += len
            length = position
        }
    }

    fun position(newPosition: Long) {
        if (position > Integer.MAX_VALUE) throw IllegalArgumentException("Position too large: $newPosition")
        position = newPosition.toInt()
    }

    fun position(): Long {
        return position.toLong()
    }

    @Throws(IOException::class)
    fun length(): Long {
        return length.toLong()
    }

    fun toByteArray(): ByteArray {
        return if (position == array.size) array else Arrays.copyOfRange(array, 0, position)
    }

    companion object {
        const val ONEOVERPHI: Long = 106039

        /** The array backing the output stream.  */
        const val DEFAULT_INITIAL_CAPACITY = 16
        fun ensureOffsetLength(arrayLength: Int, offset: Int, length: Int) {
            if (offset < 0) throw ArrayIndexOutOfBoundsException("Offset ($offset) is negative")
            if (length < 0) throw IllegalArgumentException("Length ($length) is negative")
            if (offset + length > arrayLength) throw ArrayIndexOutOfBoundsException("Last index (" + (offset + length) + ") is greater than array length (" + arrayLength + ")")
        }

        fun grow(array: ByteArray, length: Int): ByteArray {
            if (length > array.size) {
                val newLength = Math.min(Math.max(ONEOVERPHI * array.size ushr 16, length), Integer.MAX_VALUE) as Int
                val t = ByteArray(newLength)
                System.arraycopy(array, 0, t, 0, array.size)
                return t
            }
            return array
        }

        fun grow(array: ByteArray, length: Int, preserve: Int): ByteArray {
            if (length > array.size) {
                val newLength = Math.min(Math.max(ONEOVERPHI * array.size ushr 16, length), Integer.MAX_VALUE) as Int
                val t = ByteArray(newLength)
                System.arraycopy(array, 0, t, 0, preserve)
                return t
            }
            return array
        }
    }
}