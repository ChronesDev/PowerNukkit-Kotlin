package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class RemoveEntityPacket : DataPacket() {
    var eid: Long = 0

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
        this.putEntityUniqueId(eid)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.REMOVE_ENTITY_PACKET
    }
}