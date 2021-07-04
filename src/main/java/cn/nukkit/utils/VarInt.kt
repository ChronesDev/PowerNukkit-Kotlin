package cn.nukkit.utils

import cn.nukkit.api.API

/**
 * Tool class for VarInt or VarLong operations.
 *
 *
 * Some code from http://wiki.vg/Protocol.
 *
 * @author MagicDroidX
 * @author lmlstarqaq
 */
@API(usage = EXPERIMENTAL, definition = UNIVERSAL)
object VarInt {
    /**
     * @param v Signed int
     * @return Unsigned encoded int
     */
    fun encodeZigZag32(v: Int): Long {
        // Note:  the right-shift must be arithmetic
        return (v shl 1 xor (v shr 31) and 0xFFFFFFFFL).toLong()
    }

    /**
     * @param v Unsigned encoded int
     * @return Signed decoded int
     */
    fun decodeZigZag32(v: Long): Int {
        return (v shr 1).toInt() xor (-(v and 1)).toInt()
    }

    /**
     * @param v Signed long
     * @return Unsigned encoded long
     */
    fun encodeZigZag64(v: Long): Long {
        return v shl 1 xor (v shr 63)
    }

    /**
     * @param v Signed encoded long
     * @return Unsigned decoded long
     */
    fun decodeZigZag64(v: Long): Long {
        return v ushr 1 xor -(v and 1)
    }

    private fun read(stream: BinaryStream, maxSize: Int): Long {
        var value: Long = 0
        var size = 0
        var b: Int
        while (stream.getByte().also { b = it } and 0x80 == 0x80) {
            value = value or ((b and 0x7F).toLong() shl size++ * 7)
            if (size >= maxSize) {
                throw IllegalArgumentException("VarLong too big")
            }
        }
        return value or ((b and 0x7F).toLong() shl size * 7)
    }

    @Throws(IOException::class)
    private fun read(stream: InputStream, maxSize: Int): Long {
        var value: Long = 0
        var size = 0
        var b: Int
        while (stream.read().also { b = it } and 0x80 == 0x80) {
            value = value or ((b and 0x7F).toLong() shl size++ * 7)
            if (size >= maxSize) {
                throw IllegalArgumentException("VarLong too big")
            }
        }
        return value or ((b and 0x7F).toLong() shl size * 7)
    }

    /**
     * @param stream BinaryStream
     * @return Signed int
     */
    fun readVarInt(stream: BinaryStream?): Int {
        return decodeZigZag32(readUnsignedVarInt(stream))
    }

    /**
     * @param stream InputStream
     * @return Signed int
     */
    @Throws(IOException::class)
    fun readVarInt(stream: InputStream?): Int {
        return decodeZigZag32(readUnsignedVarInt(stream))
    }

    /**
     * @param stream BinaryStream
     * @return Unsigned int
     */
    fun readUnsignedVarInt(stream: BinaryStream?): Long {
        return read(stream, 5)
    }

    /**
     * @param stream InputStream
     * @return Unsigned int
     */
    @Throws(IOException::class)
    fun readUnsignedVarInt(stream: InputStream?): Long {
        return read(stream, 5)
    }

    /**
     * @param stream BinaryStream
     * @return Signed long
     */
    fun readVarLong(stream: BinaryStream?): Long {
        return decodeZigZag64(readUnsignedVarLong(stream))
    }

    /**
     * @param stream InputStream
     * @return Signed long
     */
    @Throws(IOException::class)
    fun readVarLong(stream: InputStream?): Long {
        return decodeZigZag64(readUnsignedVarLong(stream))
    }

    /**
     * @param stream BinaryStream
     * @return Unsigned long
     */
    fun readUnsignedVarLong(stream: BinaryStream?): Long {
        return read(stream, 10)
    }

    /**
     * @param stream InputStream
     * @return Unsigned long
     */
    @Throws(IOException::class)
    fun readUnsignedVarLong(stream: InputStream?): Long {
        return read(stream, 10)
    }

    private fun write(stream: BinaryStream, value: Long) {
        var value = value
        do {
            var temp = (value and 127).toByte()
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value = value ushr 7
            if (value != 0L) {
                temp = temp or 128
            }
            stream.putByte(temp)
        } while (value != 0L)
    }

    @Throws(IOException::class)
    private fun write(stream: OutputStream, value: Long) {
        var value = value
        do {
            var temp = (value and 127).toByte()
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value = value ushr 7
            if (value != 0L) {
                temp = temp or 128
            }
            stream.write(temp)
        } while (value != 0L)
    }

    /**
     * @param stream BinaryStream
     * @param value  Signed int
     */
    fun writeVarInt(stream: BinaryStream?, value: Int) {
        writeUnsignedVarInt(stream, encodeZigZag32(value))
    }

    /**
     * @param stream OutputStream
     * @param value  Signed int
     */
    @Throws(IOException::class)
    fun writeVarInt(stream: OutputStream?, value: Int) {
        writeUnsignedVarInt(stream, encodeZigZag32(value))
    }

    /**
     * @param stream BinaryStream
     * @param value  Unsigned int
     */
    fun writeUnsignedVarInt(stream: BinaryStream?, value: Long) {
        write(stream, value)
    }

    /**
     * @param stream OutputStream
     * @param value  Unsigned int
     */
    @Throws(IOException::class)
    fun writeUnsignedVarInt(stream: OutputStream?, value: Long) {
        write(stream, value)
    }

    /**
     * @param stream BinaryStream
     * @param value  Signed long
     */
    fun writeVarLong(stream: BinaryStream?, value: Long) {
        writeUnsignedVarLong(stream, encodeZigZag64(value))
    }

    /**
     * @param stream OutputStream
     * @param value  Signed long
     */
    @Throws(IOException::class)
    fun writeVarLong(stream: OutputStream?, value: Long) {
        writeUnsignedVarLong(stream, encodeZigZag64(value))
    }

    /**
     * @param stream BinaryStream
     * @param value  Unsigned long
     */
    fun writeUnsignedVarLong(stream: BinaryStream?, value: Long) {
        write(stream, value)
    }

    /**
     * @param stream OutputStream
     * @param value  Unsigned long
     */
    @Throws(IOException::class)
    fun writeUnsignedVarLong(stream: OutputStream?, value: Long) {
        write(stream, value)
    }
}