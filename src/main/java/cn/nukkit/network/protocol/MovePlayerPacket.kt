package cn.nukkit.network.protocol

import cn.nukkit.api.Since

/**
 * @since 15-10-14
 */
@ToString
class MovePlayerPacket : DataPacket() {
    var eid: Long = 0
    var x = 0f
    var y = 0f
    var z = 0f
    var yaw = 0f
    var headYaw = 0f
    var pitch = 0f
    var mode = MODE_NORMAL
    var onGround = false
    var ridingEid: Long = 0
    var int1 = 0
    var int2 = 0

    @Since("1.4.0.0-PN")
    var frame: Long = 0

    @Override
    override fun decode() {
        eid = this.getEntityRuntimeId()
        val v: Vector3f = this.getVector3f()
        x = v.x
        y = v.y
        z = v.z
        pitch = this.getLFloat()
        yaw = this.getLFloat()
        headYaw = this.getLFloat()
        mode = this.getByte()
        onGround = this.getBoolean()
        ridingEid = this.getEntityRuntimeId()
        if (mode == MODE_TELEPORT) {
            int1 = this.getLInt()
            int2 = this.getLInt()
        }
        frame = this.getUnsignedVarLong()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid)
        this.putVector3f(x, y, z)
        this.putLFloat(pitch)
        this.putLFloat(yaw)
        this.putLFloat(headYaw)
        this.putByte(mode.toByte())
        this.putBoolean(onGround)
        this.putEntityRuntimeId(ridingEid)
        if (mode == MODE_TELEPORT) {
            this.putLInt(int1)
            this.putLInt(int2)
        }
        this.putUnsignedVarLong(frame)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.MOVE_PLAYER_PACKET
        const val MODE_NORMAL = 0
        const val MODE_RESET = 1
        const val MODE_TELEPORT = 2
        const val MODE_PITCH = 3 //facepalm Mojang
    }
}