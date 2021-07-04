package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class BlockEventPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var x = 0
    var y = 0
    var z = 0
    var case1 = 0
    var case2 = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putBlockVector3(x, y, z)
        this.putVarInt(case1)
        this.putVarInt(case2)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.BLOCK_EVENT_PACKET
    }
}