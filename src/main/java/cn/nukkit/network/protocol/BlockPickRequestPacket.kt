package cn.nukkit.network.protocol

import cn.nukkit.math.BlockVector3

@ToString
class BlockPickRequestPacket : DataPacket() {
    var x = 0
    var y = 0
    var z = 0
    var addUserData = false
    var selectedSlot = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        val v: BlockVector3 = this.getSignedBlockPosition()
        x = v.x
        y = v.y
        z = v.z
        addUserData = this.getBoolean()
        selectedSlot = this.getByte()
    }

    @Override
    override fun encode() {
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.BLOCK_PICK_REQUEST_PACKET
    }
}