package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ServerSettingsRequestPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
    }
}