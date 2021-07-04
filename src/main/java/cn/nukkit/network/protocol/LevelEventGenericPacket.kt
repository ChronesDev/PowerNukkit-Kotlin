package cn.nukkit.network.protocol

import cn.nukkit.nbt.NBTIO

class LevelEventGenericPacket : DataPacket() {
    var eventId = 0
    var tag: CompoundTag? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(eventId)
        try {
            this.put(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true))
        } catch (e: IOException) {
            throw EncoderException(e)
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET
    }
}