package cn.nukkit.network.protocol

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class DataPacket : BinaryStream(), Cloneable {
    @Volatile
    var isEncoded = false
    var channel = 0
    var reliability: RakNetReliability = RakNetReliability.RELIABLE_ORDERED
    abstract fun pid(): Byte
    abstract fun decode()
    abstract fun encode()
    fun tryEncode() {
        if (!isEncoded) {
            isEncoded = true
            encode()
        }
    }

    @Override
    fun reset(): DataPacket {
        super.reset()
        this.putUnsignedVarInt(pid() and 0xff)
        return this
    }

    fun clean(): DataPacket {
        this.setBuffer(null)
        this.setOffset(0)
        isEncoded = false
        return this
    }

    @Override
    fun clone(): DataPacket? {
        return try {
            super.clone() as DataPacket?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    fun compress(): BatchPacket {
        return compress(Server.getInstance().networkCompressionLevel)
    }

    fun compress(level: Int): BatchPacket {
        val batch = BatchPacket()
        val batchPayload = arrayOfNulls<ByteArray>(2)
        val buf: ByteArray = getBuffer()
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.size)
        batchPayload[1] = buf
        try {
            batch.payload = Network.deflateRaw(batchPayload, level)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return batch
    }

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<DataPacket>(0)
    }
}