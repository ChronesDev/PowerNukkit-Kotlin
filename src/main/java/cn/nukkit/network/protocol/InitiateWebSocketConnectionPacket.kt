package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class InitiateWebSocketConnectionPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return ProtocolInfo.INITIATE_WEB_SOCKET_CONNECTION_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        //TODO
    }
}