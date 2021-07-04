package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ClientToServerHandshakePacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET
    }

    @Override
    override fun decode() {
        //no content
    }

    @Override
    override fun encode() {
    }
}