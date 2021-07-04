package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class RequestChunkRadiusPacket : DataPacket() {
    var radius = 0

    @Override
    override fun decode() {
        radius = this.getVarInt()
    }

    @Override
    override fun encode() {
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET
    }
}