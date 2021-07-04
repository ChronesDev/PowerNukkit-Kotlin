package cn.nukkit.network.protocol

import cn.nukkit.math.BlockVector3

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString(exclude = "namedTag")
class BlockEntityDataPacket : DataPacket() {
    var x = 0
    var y = 0
    var z = 0
    var namedTag: ByteArray

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        val v: BlockVector3 = this.getBlockVector3()
        x = v.x
        y = v.y
        z = v.z
        namedTag = this.get()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putBlockVector3(x, y, z)
        this.put(namedTag)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.BLOCK_ENTITY_DATA_PACKET
    }
}