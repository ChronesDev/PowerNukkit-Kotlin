package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ResourcePackChunkRequestPacket : DataPacket() {
    var packId: UUID? = null
    var chunkIndex = 0

    @Override
    override fun decode() {
        packId = UUID.fromString(this.getString())
        chunkIndex = this.getLInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(packId.toString())
        this.putLInt(chunkIndex)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET
    }
}