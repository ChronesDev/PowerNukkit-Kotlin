package cn.nukkit.nbt.stream

import cn.nukkit.utils.VarInt

/**
 * @author MagicDroidX (Nukkit Project)
 */
class NBTOutputStream(stream: OutputStream, endianness: ByteOrder, network: Boolean) : DataOutput, AutoCloseable {
    private val stream: DataOutputStream
    private val endianness: ByteOrder
    val isNetwork: Boolean

    constructor(stream: OutputStream) : this(stream, ByteOrder.BIG_ENDIAN) {}
    constructor(stream: OutputStream, endianness: ByteOrder) : this(stream, endianness, false) {}

    fun getEndianness(): ByteOrder {
        return endianness
    }

    @Override
    @Throws(IOException::class)
    fun write(bytes: ByteArray?) {
        stream.write(bytes)
    }

    @Override
    @Throws(IOException::class)
    fun write(b: ByteArray?, off: Int, len: Int) {
        stream.write(b, off, len)
    }

    @Override
    @Throws(IOException::class)
    fun write(b: Int) {
        stream.write(b)
    }

    @Override
    @Throws(IOException::class)
    fun writeBoolean(v: Boolean) {
        stream.writeBoolean(v)
    }

    @Override
    @Throws(IOException::class)
    fun writeByte(v: Int) {
        stream.writeByte(v)
    }

    @Override
    @Throws(IOException::class)
    fun writeShort(v: Int) {
        var v = v
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) shr 16
        }
        stream.writeShort(v)
    }

    @Override
    @Throws(IOException::class)
    fun writeChar(v: Int) {
        var v = v
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes(v.toChar())
        }
        stream.writeChar(v)
    }

    @Override
    @Throws(IOException::class)
    fun writeInt(v: Int) {
        var v = v
        if (isNetwork) {
            VarInt.writeVarInt(stream, v)
        } else {
            if (endianness === ByteOrder.LITTLE_ENDIAN) {
                v = Integer.reverseBytes(v)
            }
            stream.writeInt(v)
        }
    }

    @Override
    @Throws(IOException::class)
    fun writeLong(v: Long) {
        var v = v
        if (isNetwork) {
            VarInt.writeVarLong(stream, v)
        } else {
            if (endianness === ByteOrder.LITTLE_ENDIAN) {
                v = Long.reverseBytes(v)
            }
            stream.writeLong(v)
        }
    }

    @Override
    @Throws(IOException::class)
    fun writeFloat(v: Float) {
        var i: Int = Float.floatToIntBits(v)
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i)
        }
        stream.writeInt(i)
    }

    @Override
    @Throws(IOException::class)
    fun writeDouble(v: Double) {
        var l: Long = Double.doubleToLongBits(v)
        if (endianness === ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l)
        }
        stream.writeLong(l)
    }

    @Override
    @Throws(IOException::class)
    fun writeBytes(s: String?) {
        stream.writeBytes(s)
    }

    @Override
    @Throws(IOException::class)
    fun writeChars(s: String?) {
        stream.writeChars(s)
    }

    @Override
    @Throws(IOException::class)
    fun writeUTF(s: String) {
        val bytes: ByteArray = s.getBytes(StandardCharsets.UTF_8)
        if (isNetwork) {
            VarInt.writeUnsignedVarInt(stream, bytes.size)
        } else {
            writeShort(bytes.size)
        }
        stream.write(bytes)
    }

    @Override
    @Throws(IOException::class)
    fun close() {
        stream.close()
    }

    init {
        this.stream = if (stream is DataOutputStream) stream as DataOutputStream else DataOutputStream(stream)
        this.endianness = endianness
        isNetwork = network
    }
}