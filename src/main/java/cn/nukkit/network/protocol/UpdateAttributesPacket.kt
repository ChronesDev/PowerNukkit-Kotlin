package cn.nukkit.network.protocol

import cn.nukkit.api.Since

/**
 * @author Nukkit Project Team
 */
@ToString
class UpdateAttributesPacket : DataPacket() {
    var entries: Array<Attribute>?
    var entityId: Long = 0

    @Since("1.4.0.0-PN")
    var frame: Long = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    override fun decode() {}
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(entityId)
        if (entries == null) {
            this.putUnsignedVarInt(0)
        } else {
            this.putUnsignedVarInt(entries!!.size)
            for (entry in entries!!) {
                this.putLFloat(entry.getMinValue())
                this.putLFloat(entry.getMaxValue())
                this.putLFloat(entry.getValue())
                this.putLFloat(entry.getDefaultValue())
                this.putString(entry.getName())
            }
        }
        this.putUnsignedVarInt(frame)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.UPDATE_ATTRIBUTES_PACKET
    }
}