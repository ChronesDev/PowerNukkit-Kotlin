package cn.nukkit.network.protocol

import cn.nukkit.math.BlockVector3

/**
 * @author Nukkit Project Team
 */
@ToString
class PlayerActionPacket : DataPacket() {
    var entityId: Long = 0
    var action = 0
    var x = 0
    var y = 0
    var z = 0
    var face = 0

    @Override
    override fun decode() {
        entityId = this.getEntityRuntimeId()
        action = this.getVarInt()
        val v: BlockVector3 = this.getBlockVector3()
        x = v.x
        y = v.y
        z = v.z
        face = this.getVarInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(entityId)
        this.putVarInt(action)
        this.putBlockVector3(x, y, z)
        this.putVarInt(face)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.PLAYER_ACTION_PACKET
        const val ACTION_START_BREAK = 0
        const val ACTION_ABORT_BREAK = 1
        const val ACTION_STOP_BREAK = 2
        const val ACTION_GET_UPDATED_BLOCK = 3
        const val ACTION_DROP_ITEM = 4
        const val ACTION_START_SLEEPING = 5
        const val ACTION_STOP_SLEEPING = 6
        const val ACTION_RESPAWN = 7
        const val ACTION_JUMP = 8
        const val ACTION_START_SPRINT = 9
        const val ACTION_STOP_SPRINT = 10
        const val ACTION_START_SNEAK = 11
        const val ACTION_STOP_SNEAK = 12
        const val ACTION_DIMENSION_CHANGE_REQUEST = 13 //sent when dying in different dimension
        const val ACTION_DIMENSION_CHANGE_ACK = 14 //sent when spawning in a different dimension to tell the server we spawned
        const val ACTION_START_GLIDE = 15
        const val ACTION_STOP_GLIDE = 16
        const val ACTION_BUILD_DENIED = 17
        const val ACTION_CONTINUE_BREAK = 18
        const val ACTION_SET_ENCHANTMENT_SEED = 20
        const val ACTION_START_SWIMMING = 21
        const val ACTION_STOP_SWIMMING = 22
        const val ACTION_START_SPIN_ATTACK = 23
        const val ACTION_STOP_SPIN_ATTACK = 24
        const val ACTION_INTERACT_BLOCK = 25
    }
}