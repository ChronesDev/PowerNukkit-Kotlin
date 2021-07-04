package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class EntityFallPacket : DataPacket() {
    var eid: Long = 0
    var fallDistance = 0f
    var unknown = false

    @Override
    override fun decode() {
        eid = this.getEntityRuntimeId()
        fallDistance = this.getLFloat()
        unknown = this.getBoolean()
    }

    @Override
    override fun encode() {
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.ENTITY_FALL_PACKET
    }
}