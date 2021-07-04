package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString(exclude = "data")
class LevelChunkPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var chunkX = 0
    var chunkZ = 0
    var subChunkCount = 0
    var cacheEnabled = false
    var blobIds: LongArray
    var data: ByteArray

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(chunkX)
        this.putVarInt(chunkZ)
        this.putUnsignedVarInt(subChunkCount)
        this.putBoolean(cacheEnabled)
        if (cacheEnabled) {
            this.putUnsignedVarInt(blobIds.size)
            for (blobId in blobIds) {
                this.putLLong(blobId)
            }
        }
        this.putByteArray(data)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.FULL_CHUNK_DATA_PACKET
    }
}