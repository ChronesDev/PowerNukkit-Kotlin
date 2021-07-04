package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class SubClientLoginPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return ProtocolInfo.SUB_CLIENT_LOGIN_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        //TODO
    }
}