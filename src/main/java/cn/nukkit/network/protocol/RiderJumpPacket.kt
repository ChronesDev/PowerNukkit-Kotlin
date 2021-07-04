package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class RiderJumpPacket : DataPacket() {
    var unknown = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        unknown = this.getVarInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(unknown)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.RIDER_JUMP_PACKET
    }
}