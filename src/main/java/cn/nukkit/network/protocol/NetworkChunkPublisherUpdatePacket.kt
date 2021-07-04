package cn.nukkit.network.protocol

import cn.nukkit.math.BlockVector3

@ToString
class NetworkChunkPublisherUpdatePacket : DataPacket() {
    var position: BlockVector3? = null
    var radius = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.NETWORK_CHUNK_PUBLISHER_UPDATE_PACKET
    }

    @Override
    override fun decode() {
        position = this.getSignedBlockPosition()
        radius = this.getUnsignedVarInt() as Int
    }

    @Override
    override fun encode() {
        this.reset()
        this.putSignedBlockPosition(position)
        this.putUnsignedVarInt(radius)
    }
}