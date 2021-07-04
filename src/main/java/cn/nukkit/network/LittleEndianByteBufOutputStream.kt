package cn.nukkit.network

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class LittleEndianByteBufOutputStream @Since("1.4.0.0-PN") constructor(@Nonnull buffer: ByteBuf) : ByteBufOutputStream(buffer) {
    private val buffer: ByteBuf
    @Override
    @Throws(IOException::class)
    fun writeChar(v: Int) {
        buffer.writeChar(Character.reverseBytes(v.toChar()))
    }

    @Override
    @Throws(IOException::class)
    fun writeDouble(v: Double) {
        buffer.writeDoubleLE(v)
    }

    @Override
    @Throws(IOException::class)
    fun writeFloat(v: Float) {
        buffer.writeFloatLE(v)
    }

    @Override
    @Throws(IOException::class)
    fun writeShort(`val`: Int) {
        buffer.writeShortLE(`val`)
    }

    @Override
    @Throws(IOException::class)
    fun writeLong(`val`: Long) {
        buffer.writeLongLE(`val`)
    }

    @Override
    @Throws(IOException::class)
    fun writeInt(`val`: Int) {
        buffer.writeIntLE(`val`)
    }

    @Override
    @Throws(IOException::class)
    fun writeUTF(string: String) {
        val bytes: ByteArray = string.getBytes(StandardCharsets.UTF_8)
        writeShort(bytes.size)
        this.write(bytes)
    }

    init {
        this.buffer = buffer
    }
}