package cn.nukkit.nbt.stream

import cn.nukkit.utils.VarInt

/**
 * @author MagicDroidX (Nukkit Project)
 */
class NBTInputStream(stream: InputStream, endianness: ByteOrder, network: Boolean) : DataInput, AutoCloseable {
    private val stream: DataInputStream
    private val endianness: ByteOrder
    val isNetwork: Boolean

    constructor(stream: InputStream) : this(stream, ByteOrder.BIG_ENDIAN) {}
    constructor(stream: InputStream, endianness: ByteOrder) : this(stream, endianness, false) {}

    fun getEndianness(): ByteOrder {
        return endianness
    }

    @Override
    @Throws(IOException::class)
    fun readFully(b: ByteArray?) {
        stream.readFully(b)
    }

    @Override
    @Throws(IOException::class)
    fun readFully(b: ByteArray?, off: Int, len: Int) {
        stream.readFully(b, off, len)
    }

    @Override
    @Throws(IOException::class)
    fun skipBytes(n: Int): Int {
        return stream.skipBytes(n)
    }

    @Override
    @Throws(IOException::class)
    fun readBoolean(): Boolean {
        return stream.readBoolean()
    }

    @Override
    @Throws(IOException::class)
    fun readByte(): Byte {
        return stream.readByte()
    }

    @Override
    @Throws(IOException::class)
    fun readUnsignedByte(): Int {
        return stream.readUnsignedByte()
    }

    @Override
    @Throws(IOException::class)
    fun readShort(): Short {
        var s: Short = stream.readShort()
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            s = Short.reverseBytes(s)
        }
        return s
    }

    @Override
    @Throws(IOException::class)
    fun readUnsignedShort(): Int {
        var s: Int = stream.readUnsignedShort()
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            s = Integer.reverseBytes(s) shr 16
        }
        return s
    }

    @Override
    @Throws(IOException::class)
    fun readChar(): Char {
        var c: Char = stream.readChar()
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            c = Character.reverseBytes(c)
        }
        return c
    }

    @Override
    @Throws(IOException::class)
    fun readInt(): Int {
        if (isNetwork) {
            return VarInt.readVarInt(stream)
        }
        var i: Int = stream.readInt()
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i)
        }
        return i
    }

    @Override
    @Throws(IOException::class)
    fun readLong(): Long {
        if (isNetwork) {
            return VarInt.readVarLong(stream)
        }
        var l: Long = stream.readLong()
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l)
        }
        return l
    }

    @Override
    @Throws(IOException::class)
    fun readFloat(): Float {
        var i: Int = stream.readInt()
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i)
        }
        return Float.intBitsToFloat(i)
    }

    @Override
    @Throws(IOException::class)
    fun readDouble(): Double {
        var l: Long = stream.readLong()
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l)
        }
        return Double.longBitsToDouble(l)
    }

    @Override
    @Deprecated
    @Throws(IOException::class)
    fun readLine(): String {
        return stream.readLine()
    }

    @Override
    @Throws(IOException::class)
    fun readUTF(): String {
        val length = if (isNetwork) VarInt.readUnsignedVarInt(stream) else readUnsignedShort()
        val bytes = ByteArray(length)
        stream.readFully(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    @Throws(IOException::class)
    fun available(): Int {
        return stream.available()
    }

    @Override
    @Throws(IOException::class)
    fun close() {
        stream.close()
    }

    init {
        this.stream = if (stream is DataInputStream) stream as DataInputStream else DataInputStream(stream)
        this.endianness = endianness
        isNetwork = network
    }
}