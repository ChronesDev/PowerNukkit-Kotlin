package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class ChunkRadiusUpdatedPacket : DataPacket() {
    var radius = 0

    @Override
    override fun decode() {
        radius = this.getVarInt()
    }

    @Override
    override fun encode() {
        super.reset()
        this.putVarInt(radius)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET
    }
}