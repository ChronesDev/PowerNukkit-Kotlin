package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class SimpleEventPacket : DataPacket() {
    var unknown: Short = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.SIMPLE_EVENT_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putShort(unknown)
    }
}