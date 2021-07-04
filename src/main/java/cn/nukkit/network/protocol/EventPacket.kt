package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class EventPacket : DataPacket() {
    var eid: Long = 0
    var unknown1 = 0
    var unknown2: Byte = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.EVENT_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarLong(eid)
        this.putVarInt(unknown1)
        this.putByte(unknown2)
    }
}