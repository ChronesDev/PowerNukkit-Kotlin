package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class UpdateBlockPacket : DataPacket() {
    var x = 0
    var z = 0
    var y = 0
    var blockRuntimeId = 0
    var flags = 0
    var dataLayer = 0

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
        this.putBlockVector3(x, y, z)
        this.putUnsignedVarInt(blockRuntimeId)
        this.putUnsignedVarInt(flags)
        this.putUnsignedVarInt(dataLayer)
    }

    class Entry(val x: Int, val z: Int, val y: Int, val blockId: Int, val blockData: Int, val flags: Int)
    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.UPDATE_BLOCK_PACKET
        const val FLAG_NONE = 0
        const val FLAG_NEIGHBORS = 1
        const val FLAG_NETWORK = 2
        const val FLAG_NOGRAPHIC = 4
        const val FLAG_PRIORITY = 8
        const val FLAG_ALL = FLAG_NEIGHBORS or FLAG_NETWORK
        const val FLAG_ALL_PRIORITY = FLAG_ALL or FLAG_PRIORITY
    }
}