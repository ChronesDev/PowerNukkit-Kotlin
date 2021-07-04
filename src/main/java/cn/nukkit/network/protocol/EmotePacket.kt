package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@ToString
class EmotePacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var runtimeId: Long = 0

    @Since("1.3.0.0-PN")
    var emoteID: String? = null

    @Since("1.3.0.0-PN")
    var flags: Byte = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        runtimeId = this.getEntityRuntimeId()
        emoteID = this.getString()
        flags = this.getByte() as Byte
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(runtimeId)
        this.putString(emoteID)
        this.putByte(flags)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.EMOTE_PACKET
    }
}