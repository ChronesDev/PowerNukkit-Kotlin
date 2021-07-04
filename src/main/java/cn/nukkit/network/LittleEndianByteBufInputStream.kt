package cn.nukkit.network

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class LittleEndianByteBufInputStream @Since("1.4.0.0-PN") constructor(@Nonnull buffer: ByteBuf) : ByteBufInputStream(buffer) {
    private val buffer: ByteBuf
    @Override
    @Throws(IOException::class)
    fun readChar(): Char {
        return Character.reverseBytes(buffer.readChar())
    }

    @Override
    @Throws(IOException::class)
    fun readDouble(): Double {
        return buffer.readDoubleLE()
    }

    @Override
    @Throws(IOException::class)
    fun readFloat(): Float {
        return buffer.readFloatLE()
    }

    @Override
    @Throws(IOException::class)
    fun readShort(): Short {
        return buffer.readShortLE()
    }

    @Override
    @Throws(IOException::class)
    fun readUnsignedShort(): Int {
        return buffer.readUnsignedShortLE()
    }

    @Override
    @Throws(IOException::class)
    fun readLong(): Long {
        return buffer.readLongLE()
    }

    @Override
    @Throws(IOException::class)
    fun readInt(): Int {
        return buffer.readIntLE()
    }

    init {
        this.buffer = buffer
    }
}