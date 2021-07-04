package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ServerSettingsResponsePacket : DataPacket() {
    var formId = 0
    var data: String? = null

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.SERVER_SETTINGS_RESPONSE_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(formId)
        this.putString(data)
    }
}