package cn.nukkit.network.protocol

import cn.nukkit.entity.data.EntityMetadata

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class AddPlayerPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var uuid: UUID? = null
    var username: String? = null
    var entityUniqueId: Long = 0
    var entityRuntimeId: Long = 0
    var platformChatId = ""
    var x = 0f
    var y = 0f
    var z = 0f
    var speedX = 0f
    var speedY = 0f
    var speedZ = 0f
    var pitch = 0f
    var yaw = 0f
    var item: Item? = null
    var metadata: EntityMetadata = EntityMetadata()

    //public EntityLink links = new EntityLink[0];
    var deviceId = ""
    var buildPlatform = -1

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putUUID(uuid)
        this.putString(username)
        this.putEntityUniqueId(entityUniqueId)
        this.putEntityRuntimeId(entityRuntimeId)
        this.putString(platformChatId)
        this.putVector3f(x, y, z)
        this.putVector3f(speedX, speedY, speedZ)
        this.putLFloat(pitch)
        this.putLFloat(yaw) //TODO headrot
        this.putLFloat(yaw)
        this.putSlot(item)
        this.put(Binary.writeMetadata(metadata))
        this.putUnsignedVarInt(0) //TODO: Adventure settings
        this.putUnsignedVarInt(0)
        this.putUnsignedVarInt(0)
        this.putUnsignedVarInt(0)
        this.putUnsignedVarInt(0)
        this.putLLong(entityUniqueId)
        this.putUnsignedVarInt(0) //TODO: Entity links
        this.putString(deviceId)
        this.putLInt(buildPlatform)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.ADD_PLAYER_PACKET
    }
}