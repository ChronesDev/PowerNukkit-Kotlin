package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author Nukkit Project Team
 */
@ToString
class AddPaintingPacket : DataPacket() {
    var entityUniqueId: Long = 0
    var entityRuntimeId: Long = 0
    var x = 0f
    var y = 0f
    var z = 0f
    var direction = 0
    var title: String? = null

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityUniqueId(entityUniqueId)
        this.putEntityRuntimeId(entityRuntimeId)
        this.putVector3f(x, y, z)
        this.putVarInt(direction)
        this.putString(title)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.ADD_PAINTING_PACKET
    }
}