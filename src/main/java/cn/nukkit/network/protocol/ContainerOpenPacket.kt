package cn.nukkit.network.protocol

import cn.nukkit.math.BlockVector3

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class ContainerOpenPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var windowId = 0
    var type = 0
    var x = 0
    var y = 0
    var z = 0
    var entityId: Long = -1

    @Override
    override fun decode() {
        windowId = this.getByte()
        type = this.getByte()
        val v: BlockVector3 = this.getBlockVector3()
        x = v.x
        y = v.y
        z = v.z
        entityId = this.getEntityUniqueId()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(windowId.toByte())
        this.putByte(type.toByte())
        this.putBlockVector3(x, y, z)
        this.putEntityUniqueId(entityId)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CONTAINER_OPEN_PACKET
    }
}