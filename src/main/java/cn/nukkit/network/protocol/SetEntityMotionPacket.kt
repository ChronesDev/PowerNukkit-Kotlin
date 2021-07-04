package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class SetEntityMotionPacket : DataPacket() {
    var eid: Long = 0
    var motionX = 0f
    var motionY = 0f
    var motionZ = 0f

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
        this.putEntityRuntimeId(eid)
        this.putVector3f(motionX, motionY, motionZ)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_ENTITY_MOTION_PACKET
    }
}