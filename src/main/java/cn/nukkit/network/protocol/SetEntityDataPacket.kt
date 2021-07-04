package cn.nukkit.network.protocol

import cn.nukkit.api.Since

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class SetEntityDataPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var eid: Long = 0
    var metadata: EntityMetadata? = null

    @Since("1.4.0.0-PN")
    var frame: Long = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid)
        this.put(Binary.writeMetadata(metadata))
        this.putUnsignedVarLong(frame)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_ENTITY_DATA_PACKET
    }
}