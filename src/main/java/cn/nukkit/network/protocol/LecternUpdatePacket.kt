package cn.nukkit.network.protocol

import cn.nukkit.math.BlockVector3

@ToString
class LecternUpdatePacket : DataPacket() {
    var page = 0
    var totalPages = 0
    var blockPosition: BlockVector3? = null
    var dropBook = false

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        page = this.getByte()
        totalPages = this.getByte()
        blockPosition = this.getBlockVector3()
        dropBook = this.getBoolean()
    }

    @Override
    override fun encode() {
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.LECTERN_UPDATE_PACKET
    }
}