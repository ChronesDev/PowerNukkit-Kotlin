package cn.nukkit.network.protocol

import cn.nukkit.math.Vector3f

/**
 * @author Nukkit Project Team
 */
@ToString
class RespawnPacket : DataPacket() {
    var x = 0f
    var y = 0f
    var z = 0f
    var respawnState = STATE_SEARCHING_FOR_SPAWN
    var runtimeEntityId: Long = 0

    @Override
    override fun decode() {
        val v: Vector3f = this.getVector3f()
        x = v.x
        y = v.y
        z = v.z
        respawnState = this.getByte()
        runtimeEntityId = this.getEntityRuntimeId()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVector3f(x, y, z)
        this.putByte(respawnState.toByte())
        this.putEntityRuntimeId(runtimeEntityId)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.RESPAWN_PACKET
        const val STATE_SEARCHING_FOR_SPAWN = 0
        const val STATE_READY_TO_SPAWN = 1
        const val STATE_CLIENT_READY_TO_SPAWN = 2
    }
}