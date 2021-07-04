package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.3.0.0-PN")
@ToString
class DebugInfoPacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var entityId: Long = 0

    @Since("1.3.0.0-PN")
    var data: String? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        entityId = this.getLong()
        data = this.getString()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putLong(entityId)
        this.putString(data)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.DEBUG_INFO_PACKET
    }
}