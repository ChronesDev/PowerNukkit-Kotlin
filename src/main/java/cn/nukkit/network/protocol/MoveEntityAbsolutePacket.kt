package cn.nukkit.network.protocol

import cn.nukkit.math.Vector3f

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class MoveEntityAbsolutePacket : DataPacket() {
    var eid: Long = 0
    var x = 0.0
    var y = 0.0
    var z = 0.0
    var yaw = 0.0
    var headYaw = 0.0
    var pitch = 0.0
    var onGround = false
    var teleport = false

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        eid = this.getEntityRuntimeId()
        val flags: Int = this.getByte()
        teleport = flags and 0x01 != 0
        onGround = flags and 0x02 != 0
        val v: Vector3f = this.getVector3f()
        x = v.x
        y = v.y
        z = v.z
        pitch = this.getByte() * (360.0 / 256.0)
        headYaw = this.getByte() * (360.0 / 256.0)
        yaw = this.getByte() * (360.0 / 256.0)
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid)
        var flags: Byte = 0
        if (onGround) {
            flags = flags or 0x01
        }
        if (teleport) {
            flags = flags or 0x02
        }
        this.putByte(flags)
        this.putVector3f(x.toFloat(), y.toFloat(), z.toFloat())
        this.putByte((pitch / (360.0 / 256.0)).toByte())
        this.putByte((headYaw / (360.0 / 256.0)).toByte())
        this.putByte((yaw / (360.0 / 256.0)).toByte())
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET
    }
}