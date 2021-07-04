package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class SetTimePacket : DataPacket() {
    var time = 0

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
        this.putVarInt(time)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_TIME_PACKET
    }
}