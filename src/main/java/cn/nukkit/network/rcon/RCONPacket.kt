package cn.nukkit.network.rcon

import java.io.IOException

/**
 * A data structure representing an RCON packet.
 *
 * @author Tee7even
 */
class RCONPacket {
    val id: Int
    val type: Int
    val payload: ByteArray

    constructor(id: Int, type: Int, payload: ByteArray) {
        this.id = id
        this.type = type
        this.payload = payload
    }

    constructor(buffer: ByteBuffer) {
        val size: Int = buffer.getInt()
        id = buffer.getInt()
        type = buffer.getInt()
        payload = ByteArray(size - 10)
        buffer.get(payload)
        buffer.get(ByteArray(2))
    }

    fun toBuffer(): ByteBuffer {
        val buffer: ByteBuffer = ByteBuffer.allocate(payload.size + 14)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.putInt(payload.size + 10)
        buffer.putInt(id)
        buffer.putInt(type)
        buffer.put(payload)
        buffer.put(0.toByte())
        buffer.put(0.toByte())
        buffer.flip()
        return buffer
    }
}