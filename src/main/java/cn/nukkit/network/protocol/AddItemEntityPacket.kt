package cn.nukkit.network.protocol

import cn.nukkit.entity.data.EntityMetadata

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class AddItemEntityPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var entityUniqueId: Long = 0
    var entityRuntimeId: Long = 0
    var item: Item? = null
    var x = 0f
    var y = 0f
    var z = 0f
    var speedX = 0f
    var speedY = 0f
    var speedZ = 0f
    var metadata: EntityMetadata = EntityMetadata()
    var isFromFishing = false

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityUniqueId(entityUniqueId)
        this.putEntityRuntimeId(entityRuntimeId)
        this.putSlot(item)
        this.putVector3f(x, y, z)
        this.putVector3f(speedX, speedY, speedZ)
        this.put(Binary.writeMetadata(metadata))
        this.putBoolean(isFromFishing)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.ADD_ITEM_ENTITY_PACKET
    }
}