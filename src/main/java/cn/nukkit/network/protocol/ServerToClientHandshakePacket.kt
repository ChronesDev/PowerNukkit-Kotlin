package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ServerToClientHandshakePacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return ProtocolInfo.SERVER_TO_CLIENT_HANDSHAKE_PACKET
    }

    var publicKey: String? = null
    var serverToken: String? = null
    var privateKey: String? = null

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        //TODO
    }
}