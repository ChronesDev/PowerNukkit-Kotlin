package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class SetLastHurtByPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return ProtocolInfo.SET_LAST_HURT_BY_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        //TODO
    }
}